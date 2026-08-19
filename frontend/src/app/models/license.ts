export type LicenseStatus = 'ACTIVE' | 'EXPIRED' | 'DELETED';

export interface LicenseDto {
  id?: number;
  name: string;
  startDate: string;
  endDate: string;
  status: LicenseStatus;
}
