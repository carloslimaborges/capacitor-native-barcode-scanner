import { MpdftBarcodeScannerPlugin } from 'capacitor-native-barcode-scanner';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    MpdftBarcodeScannerPlugin.echo({ value: inputValue })
}
