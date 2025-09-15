import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';
export declare class PatientManagementApi {
    readonly httpRequest: BaseHttpRequest;
    constructor(httpRequest: BaseHttpRequest);
    /**
     * Get all patients
     * @returns any List of patients
     * @throws ApiError
     */
    getAllPacientes(): CancelablePromise<any>;
}
