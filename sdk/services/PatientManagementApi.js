"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
exports.PatientManagementApi = void 0;
class PatientManagementApi {
    constructor(httpRequest) {
        this.httpRequest = httpRequest;
    }
    /**
     * Get all patients
     * @returns any List of patients
     * @throws ApiError
     */
    getAllPacientes() {
        return this.httpRequest.request({
            method: 'GET',
            url: '/pacientes',
        });
    }
}
exports.PatientManagementApi = PatientManagementApi;
