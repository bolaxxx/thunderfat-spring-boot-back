/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';

export class AuthenticationApi {

    constructor(public readonly httpRequest: BaseHttpRequest) {}

    /**
     * User authentication
     * @returns any Authentication successful
     * @throws ApiError
     */
    public authenticate(): CancelablePromise<any> {
        return this.httpRequest.request({
            method: 'POST',
            url: '/auth/login',
        });
    }

}
