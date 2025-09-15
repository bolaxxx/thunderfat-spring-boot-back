/* generated using openapi-typescript-codegen -- do no edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { CancelablePromise } from '../core/CancelablePromise';
import type { BaseHttpRequest } from '../core/BaseHttpRequest';

export class PatientManagementApi {

    constructor(public readonly httpRequest: BaseHttpRequest) {}

    /**
     * Get all patients
     * @returns any List of patients
     * @throws ApiError
     */
    public getAllPacientes(): CancelablePromise<any> {
        return this.httpRequest.request({
            method: 'GET',
            url: '/pacientes',
        });
    }

}
