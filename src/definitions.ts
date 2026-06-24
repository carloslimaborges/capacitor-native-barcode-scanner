export interface MpdftBarcodeScannerPluginPlugin {
  echo(options: { value: string }): Promise<{ value: string }>;
}
