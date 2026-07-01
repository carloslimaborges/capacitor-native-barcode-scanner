import { WebPlugin } from '@capacitor/core';

import type { MpdftBarcodeScannerPlugin, ScanResult } from './definitions';

export class MpdftBarcodeScannerWeb extends WebPlugin implements MpdftBarcodeScannerPlugin {
  async scan(): Promise<ScanResult> {
    throw this.unimplemented('MpdftBarcodeScanner is not available on web.');
  }
}
