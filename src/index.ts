import { registerPlugin } from '@capacitor/core';

import type { MpdftBarcodeScannerPlugin } from './definitions';

const MpdftBarcodeScanner = registerPlugin<MpdftBarcodeScannerPlugin>('MpdftBarcodeScanner', {
  web: () => import('./web').then((m) => new m.MpdftBarcodeScannerWeb()),
});

export * from './definitions';
export { MpdftBarcodeScanner };
