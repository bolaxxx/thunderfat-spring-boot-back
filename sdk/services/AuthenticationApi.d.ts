import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export declare class AuthenticationApi {
    readonly httpRequest: BaseHttpRequest;
    constructor(httpRequest: BaseHttpRequest);
    /**
     * User authentication
     * @returns any Authentication successful
     * @throws ApiError
     */
    authenticate(): CancelablePromise<any>;
}
