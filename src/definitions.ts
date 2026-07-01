export interface MpdftBarcodeScannerPlugin {
  scan(): Promise<ScanResult>;
}

export interface ScanResult {
  value: string;
  format: string;
  cancelled: boolean;
}
