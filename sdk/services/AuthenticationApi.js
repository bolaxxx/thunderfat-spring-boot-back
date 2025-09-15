"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.AuthenticationApi = void 0;
class AuthenticationApi {
    constructor(httpRequest) {
        this.httpRequest = httpRequest;
    }
    /**
     * User authentication
     * @returns any Authentication successful
     * @throws ApiError
     */
    authenticate() {
        return this.httpRequest.request({
            method: 'POST',
            url: '/auth/login',
        });
    }
}
exports.AuthenticationApi = AuthenticationApi;
