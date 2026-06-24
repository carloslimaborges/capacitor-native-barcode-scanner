package com.github.carloslimaborges.barcodescanner;

import com.getcapacitor.Logger;

public class MpdftBarcodeScannerPlugin {

    public String echo(String value) {
        Logger.info("Echo", value);
        return value;
    }
}
