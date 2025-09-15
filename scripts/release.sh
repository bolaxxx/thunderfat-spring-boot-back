#!/bin/bash

# release.sh - Script to manage releases with semantic versioning

set -e

CURRENT_BRANCH=$(git branch --show-current)
BUMP_TYPE=${1:-patch}
DRY_RUN=${2:-false}

echo "🚀 Starting release process..."

# Validate inputs
validate_inputs() {
    if [[ ! "$BUMP_TYPE" =~ ^(major|minor|patch)$ ]]; then
        echo "❌ Invalid bump type: $BUMP_TYPE"
        echo "Usage: $0 [major|minor|patch] [dry-run]"
        exit 1
    fi
    
    if [[ "$CURRENT_BRANCH" != "master" && "$CURRENT_BRANCH" != "main" ]]; then
        echo "❌ Releases can only be created from master/main branch"
        echo "Current branch: $CURRENT_BRANCH"
        exit 1
    fi
}

# Check if working directory is clean
check_working_directory() {
    if [ "$DRY_RUN" != "true" ] && [ -n "$(git status --porcelain)" ]; then
        echo "❌ Working directory is not clean. Please commit or stash changes."
        git status --short
        exit 1
    fi
}

# Get current version from pom.xml (project version, not parent)
get_current_version() {
    # Get the project version (after parent section)
    CURRENT_VERSION=$(sed -n '/<\/parent>/,/<properties>/p' pom.xml | grep -oP '<version>\K[^<-]+' | head -1)
    if [ -z "$CURRENT_VERSION" ]; then
        # Fallback: get version after groupId line
        CURRENT_VERSION=$(sed -n '/<groupId>com\.thunderfat<\/groupId>/,/<properties>/p' pom.xml | grep -oP '<version>\K[^<-]+' | head -1)
    fi
    echo "📦 Current version: $CURRENT_VERSION"
}

# Calculate new version
calculate_new_version() {
    case "$BUMP_TYPE" in
        "major")
            NEW_VERSION=$(echo $CURRENT_VERSION | awk -F. '{print ($1+1)".0.0"}')
            ;;
        "minor")
            NEW_VERSION=$(echo $CURRENT_VERSION | awk -F. '{print $1"."($2+1)".0"}')
            ;;
        "patch")
            NEW_VERSION=$(echo $CURRENT_VERSION | awk -F. '{print $1"."$2"."($3+1)}')
            ;;
    esac
    echo "📦 New version: $NEW_VERSION"
}

# Update version in files
update_version_files() {
    echo "📝 Updating version files..."
    
    if [ "$DRY_RUN" != "true" ]; then
        # Update pom.xml (only project version, not parent version)
        sed -i "/com\.thunderfat/,/<properties>/ s/<version>$CURRENT_VERSION<\/version>/<version>$NEW_VERSION<\/version>/" pom.xml
        echo "✅ Updated pom.xml"
        
        # Update CHANGELOG_API.md
        update_changelog
    else
        echo "🏃 DRY RUN: Would update pom.xml and CHANGELOG_API.md"
    fi
}

# Update changelog
update_changelog() {
    DATE=$(date +%Y-%m-%d)
    TEMP_CHANGELOG=$(mktemp)
    
    # Create new changelog entry
    cat > $TEMP_CHANGELOG << EOF
# ThunderFat API Changelog

All notable changes to the ThunderFat API will be documented in this file.

## [$NEW_VERSION] - $DATE

### Changed
- Version bump to $NEW_VERSION ($BUMP_TYPE release)
- API specification updated
- SDK regenerated with latest changes

$(tail -n +4 CHANGELOG_API.md)
EOF
    
    mv $TEMP_CHANGELOG CHANGELOG_API.md
    echo "✅ Updated CHANGELOG_API.md"
}

# Export API specification
export_api_spec() {
    echo "📄 Exporting API specification..."
    
    if [ "$DRY_RUN" != "true" ]; then
        if ./scripts/export-api-spec.sh; then
            echo "✅ API specification exported"
        else
            echo "❌ Failed to export API specification"
            exit 1
        fi
    else
        echo "🏃 DRY RUN: Would export API specification"
    fi
}

# Generate SDK
generate_sdk() {
    echo "🔧 Generating SDK..."
    
    if [ "$DRY_RUN" != "true" ]; then
        if ./scripts/generate-sdk.sh api/openapi.json sdk @thunderfat/backend-sdk $NEW_VERSION; then
            echo "✅ SDK generated"
        else
            echo "❌ Failed to generate SDK"
            exit 1
        fi
    else
        echo "🏃 DRY RUN: Would generate SDK"
    fi
}

# Create git tag and commit
create_git_tag() {
    echo "🏷️  Creating git tag..."
    
    if [ "$DRY_RUN" != "true" ]; then
        # Add changed files
        git add pom.xml CHANGELOG_API.md api/openapi.json
        
        # Commit changes
        git commit -m "chore: release v$NEW_VERSION

- Update version to $NEW_VERSION
- Update API specification
- Update changelog
- Generate SDK v$NEW_VERSION"
        
        # Create tag
        git tag -a "v$NEW_VERSION" -m "Release v$NEW_VERSION

This release includes:
- API specification updates
- SDK generation
- Version bump ($BUMP_TYPE)

For detailed changes, see CHANGELOG_API.md"
        
        echo "✅ Created tag v$NEW_VERSION"
    else
        echo "🏃 DRY RUN: Would create commit and tag v$NEW_VERSION"
    fi
}

# Push changes
push_changes() {
    echo "📤 Pushing changes..."
    
    if [ "$DRY_RUN" != "true" ]; then
        git push origin "$CURRENT_BRANCH"
        git push origin "v$NEW_VERSION"
        echo "✅ Pushed changes and tag to remote"
    else
        echo "🏃 DRY RUN: Would push changes and tag to remote"
    fi
}

# Create GitHub release
create_github_release() {
    echo "🚀 Creating GitHub release..."
    
    if [ "$DRY_RUN" != "true" ]; then
        # Check if gh CLI is available
        if command -v gh &> /dev/null; then
            gh release create "v$NEW_VERSION" \
                --title "Release v$NEW_VERSION" \
                --notes "$(extract_release_notes)" \
                --target "$CURRENT_BRANCH"
            echo "✅ GitHub release created"
        else
            echo "⚠️  GitHub CLI not found. Please create release manually:"
            echo "   https://github.com/$(git config remote.origin.url | sed 's/.*github.com[:/]\([^.]*\).*/\1/')/releases/new?tag=v$NEW_VERSION"
        fi
    else
        echo "🏃 DRY RUN: Would create GitHub release v$NEW_VERSION"
    fi
}

# Extract release notes from changelog
extract_release_notes() {
    awk "/## \[$NEW_VERSION\]/{flag=1; next} /## \[/{flag=0} flag" CHANGELOG_API.md
}

# Publish SDK to npm
publish_sdk() {
    echo "📦 Publishing SDK to npm..."
    
    if [ "$DRY_RUN" != "true" ]; then
        if [ -d "sdk" ] && [ -f "sdk/package.json" ]; then
            cd sdk
            if npm publish --access public; then
                echo "✅ SDK published to npm"
            else
                echo "⚠️  Failed to publish SDK to npm (check npm authentication)"
            fi
            cd ..
        else
            echo "⚠️  SDK directory not found, skipping npm publish"
        fi
    else
        echo "🏃 DRY RUN: Would publish SDK to npm"
    fi
}

# Generate release summary
generate_release_summary() {
    echo "📊 Generating release summary..."
    
    cat > "RELEASE_v${NEW_VERSION}_SUMMARY.md" << EOF
# Release v$NEW_VERSION Summary

## Release Information
- **Version**: $NEW_VERSION
- **Type**: $BUMP_TYPE release
- **Date**: $(date)
- **Branch**: $CURRENT_BRANCH

## Changes Made
- Updated version from $CURRENT_VERSION to $NEW_VERSION
- Exported fresh API specification
- Generated TypeScript SDK v$NEW_VERSION
- Updated CHANGELOG_API.md

## Artifacts Generated
- Git tag: v$NEW_VERSION
- API specification: api/openapi.json
- TypeScript SDK: sdk/
- npm package: @thunderfat/backend-sdk@$NEW_VERSION

## Next Steps
1. Monitor deployment pipeline
2. Update dependent projects
3. Announce release to stakeholders

## Links
- GitHub Release: https://github.com/$(git config remote.origin.url | sed 's/.*github.com[:/]\([^.]*\).*/\1/')/releases/tag/v$NEW_VERSION
- npm Package: https://www.npmjs.com/package/@thunderfat/backend-sdk/v/$NEW_VERSION
EOF
    
    echo "✅ Release summary: RELEASE_v${NEW_VERSION}_SUMMARY.md"
}

# Main execution
main() {
    echo "🎯 Release Configuration:"
    echo "   - Bump Type: $BUMP_TYPE"
    echo "   - Dry Run: $DRY_RUN"
    echo "   - Branch: $CURRENT_BRANCH"
    echo ""
    
    validate_inputs
    check_working_directory
    get_current_version
    calculate_new_version
    
    if [ "$DRY_RUN" != "true" ]; then
        read -p "🤔 Proceed with release v$NEW_VERSION? (y/N): " -n 1 -r
        echo
        if [[ ! $REPLY =~ ^[Yy]$ ]]; then
            echo "❌ Release cancelled"
            exit 1
        fi
    fi
    
    update_version_files
    export_api_spec
    generate_sdk
    create_git_tag
    push_changes
    create_github_release
    publish_sdk
    generate_release_summary
    
    echo "🎉 Release v$NEW_VERSION completed successfully!"
    echo "📋 Summary available in: RELEASE_v${NEW_VERSION}_SUMMARY.md"
}

# Show usage if no arguments
if [ $# -eq 0 ]; then
    echo "Usage: $0 [major|minor|patch] [dry-run]"
    echo ""
    echo "Examples:"
    echo "  $0 patch           # Create patch release"
    echo "  $0 minor           # Create minor release"
    echo "  $0 major           # Create major release"
    echo "  $0 patch dry-run   # Dry run for patch release"
    exit 1
fi

# Run main function
main
