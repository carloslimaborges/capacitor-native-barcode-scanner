import { WebPlugin } from '@capacitor/core';

import type { MpdftBarcodeScannerPluginPlugin } from './definitions';

export class MpdftBarcodeScannerPluginWeb extends WebPlugin implements MpdftBarcodeScannerPluginPlugin {
  async echo(options: { value: string }): Promise<{ value: string }> {
    console.log('ECHO', options);
    return options;
  }
}
