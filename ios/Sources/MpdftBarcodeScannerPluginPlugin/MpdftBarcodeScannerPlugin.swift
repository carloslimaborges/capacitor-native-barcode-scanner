import UIKit
import AVFoundation

protocol BarcodeScannerDelegate: AnyObject {
    func didScan(value: String, format: String)
    func didCancel()
}

class BarcodeScannerViewController: UIViewController {

    weak var delegate: BarcodeScannerDelegate?

    private var captureSession: AVCaptureSession?
    private var previewLayer: AVCaptureVideoPreviewLayer?
    private var found = false

    override func viewDidLoad() {
        super.viewDidLoad()
        view.backgroundColor = .black
        setupCamera()
        setupCancelButton()
    }

    private func setupCamera() {
        guard let device = AVCaptureDevice.default(.builtInWideAngleCamera, for: .video, position: .back),
              let input = try? AVCaptureDeviceInput(device: device) else {
            delegate?.didCancel()
            return
        }

        let session = AVCaptureSession()
        session.sessionPreset = .high
        session.addInput(input)

        let output = AVCaptureMetadataOutput()
        session.addOutput(output)
        output.setMetadataObjectsDelegate(self, queue: .main)
        // itf14 is the iOS identifier for the ITF (Interleaved 2 of 5) format
        output.metadataObjectTypes = [.code128, .itf14, .ean13, .ean8, .qr, .dataMatrix]

        let previewLayer = AVCaptureVideoPreviewLayer(session: session)
        previewLayer.frame = view.bounds
        previewLayer.videoGravity = .resizeAspectFill
        view.layer.addSublayer(previewLayer)
        self.previewLayer = previewLayer

        session.startRunning()
        self.captureSession = session
    }

    private func setupCancelButton() {
        let button = UIButton(type: .system)
        button.setTitle("Cancel", for: .normal)
        button.setTitleColor(.white, for: .normal)
        button.titleLabel?.font = UIFont.systemFont(ofSize: 18, weight: .medium)
        button.backgroundColor = UIColor.black.withAlphaComponent(0.65)
        button.layer.cornerRadius = 10
        button.translatesAutoresizingMaskIntoConstraints = false
        button.addTarget(self, action: #selector(cancelTapped), for: .touchUpInside)
        view.addSubview(button)

        NSLayoutConstraint.activate([
            button.centerXAnchor.constraint(equalTo: view.centerXAnchor),
            button.bottomAnchor.constraint(equalTo: view.safeAreaLayoutGuide.bottomAnchor, constant: -24),
            button.widthAnchor.constraint(equalToConstant: 140),
            button.heightAnchor.constraint(equalToConstant: 48)
        ])
    }

    override func viewDidLayoutSubviews() {
        super.viewDidLayoutSubviews()
        previewLayer?.frame = view.bounds
    }

    @objc private func cancelTapped() {
        stopSession()
        dismiss(animated: true) { [weak self] in
            self?.delegate?.didCancel()
        }
    }

    private func stopSession() {
        captureSession?.stopRunning()
    }

    override func viewWillDisappear(_ animated: Bool) {
        super.viewWillDisappear(animated)
        stopSession()
    }
}

extension BarcodeScannerViewController: AVCaptureMetadataOutputObjectsDelegate {

    func metadataOutput(
        _ output: AVCaptureMetadataOutput,
        didOutput metadataObjects: [AVMetadataObject],
        from connection: AVCaptureConnection
    ) {
        guard !found,
              let obj = metadataObjects.first as? AVMetadataMachineReadableCodeObject,
              let value = obj.stringValue else { return }

        found = true
        stopSession()

        let format = formatToString(obj.type)
        dismiss(animated: true) { [weak self] in
            self?.delegate?.didScan(value: value, format: format)
        }
    }

    private func formatToString(_ type: AVMetadataObject.ObjectType) -> String {
        switch type {
        case .code128:    return "CODE_128"
        case .itf14:      return "ITF"
        case .ean13:      return "EAN_13"
        case .ean8:       return "EAN_8"
        case .qr:         return "QR_CODE"
        case .dataMatrix: return "DATA_MATRIX"
        default:          return "UNKNOWN"
        }
    }
}
