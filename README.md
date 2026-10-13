# capacitor-native-barcode-scanner

Barcode scanning through the native camera in a Capacitor app: **AVFoundation** on iOS, **CameraX + ML Kit** on Android. Supports CODE_128, CODE_39, ITF, EAN_13, EAN_8, QR_CODE and DATA_MATRIX.

## Why it exists

The published Capacitor scanners either wrap a web-based decoder, which reads a printed CODE_128 label badly under warehouse lighting, or they pull in a heavy barcode SDK for a single screen. This plugin does the narrow thing: it opens a native camera activity, decodes with the platform's own vision stack, and returns one result.

It was written for an institutional asset-inventory app where the scanned labels are CODE_128 and ITF on printed asset tags, read at an angle, often in poor light, by people who need the scan to work on the first try.

## Design

The surface is deliberately one method. Scanning is a modal interaction with exactly three outcomes, so the API has exactly three fields:

```ts
export interface MpdftBarcodeScannerPlugin {
  scan(): Promise<ScanResult>;
}

export interface ScanResult {
  value: string;
  format: string;
  cancelled: boolean;
}
```

No streaming callbacks, no overlay configuration, no format negotiation from JavaScript. Camera lifecycle, permission prompts and orientation stay on the native side, where the platform already solves them, instead of being half-reimplemented in a web view.

On Android the scan runs in its own `BarcodeScannerActivity` built on CameraX, with ML Kit doing the decode. On iOS an `AVCaptureSession` with `AVCaptureMetadataOutput` handles both capture and decode, so there is no third-party dependency on that platform at all.

## Install

```bash
npm install capacitor-native-barcode-scanner
npx cap sync
```

```bash
yarn add capacitor-native-barcode-scanner
npx cap sync
```

## Usage

```ts
import { MpdftBarcodeScannerPlugin } from 'capacitor-native-barcode-scanner';

const result = await MpdftBarcodeScannerPlugin.scan();

if (!result.cancelled) {
  console.log(result.format, result.value);
}
```

Permissions:

- **Android**: `android.permission.CAMERA` is declared in the plugin manifest and merged into your app.
- **iOS**: add `NSCameraUsageDescription` to your app's `Info.plist`. The system prompt appears when the capture session starts.

## Status

Version 0.0.1, running in an internal production app. The API is small enough that it is unlikely to change, but it has not been through an external release cycle, so treat it as pre-1.0.

`npm run verify` builds all three targets (iOS, Android, web). Platform docs are generated from `src/definitions.ts` with Capacitor docgen.

## Licence

MIT
