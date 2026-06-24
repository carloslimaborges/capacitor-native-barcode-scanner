# capacitor-native-barcode-scanner

Barcode scanner using native camera on iOS (AVFoundation) and Android (CameraX + ML Kit). Supports CODE_128, ITF, EAN_13, EAN_8, QR_CODE and DATA_MATRIX formats.

## Install

To use npm

```bash
npm install capacitor-native-barcode-scanner
````

To use yarn

```bash
yarn add capacitor-native-barcode-scanner
```

Sync native files

```bash
npx cap sync
```

## API

<docgen-index>

* [`echo(...)`](#echo)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### echo(...)

```typescript
echo(options: { value: string; }) => Promise<{ value: string; }>
```

| Param         | Type                            |
| ------------- | ------------------------------- |
| **`options`** | <code>{ value: string; }</code> |

**Returns:** <code>Promise&lt;{ value: string; }&gt;</code>

--------------------

</docgen-api>
