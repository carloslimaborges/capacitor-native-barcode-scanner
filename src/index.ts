import { registerPlugin } from '@capacitor/core';

import type { MpdftBarcodeScannerPluginPlugin } from './definitions';

const MpdftBarcodeScannerPlugin = registerPlugin<MpdftBarcodeScannerPluginPlugin>('MpdftBarcodeScannerPlugin', {
  web: () => import('./web').then((m) => new m.MpdftBarcodeScannerPluginWeb()),
});

export * from './definitions';
export { MpdftBarcodeScannerPlugin };
