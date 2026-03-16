import { useState } from 'react';
import api from '../api/axios';

// Load Razorpay Script dynamically (only once)
function loadRazorpayScript() {
    return new Promise((resolve) => {
        if (document.getElementById('razorpay-script')) {
            resolve(true);
            return;
        }
        const script = document.createElement('script');
        script.id = 'razorpay-script';
        script.src = 'https://checkout.razorpay.com/v1/checkout.js';
        script.onload = () => resolve(true);
        script.onerror = () => resolve(false);
        document.body.appendChild(script);
    });
}

/**
 * PaymentModal — Razorpay Online Payment Integration
 *
 * Props:
 *   doctorId      - ID of selected doctor
 *   doctorName    - Display name
 *   fee           - Consultation fee (BigDecimal as string)
 *   slotDate      - "YYYY-MM-DD"
 *   slotTime      - "HH:mm"
 *   notes         - Patient notes
 *   onSuccess(appointmentDTO) - called after successful payment + booking
 *   onCancel()    - called when user closes the modal
 */
function PaymentModal({ doctorId, doctorName, specialization, fee, slotDate, slotTime, notes, onSuccess, onCancel }) {
    const [loading, setLoading] = useState(false);
    const [status, setStatus] = useState('idle'); // idle | processing | verifying | done | error
    const [errorMsg, setErrorMsg] = useState('');

    const handlePayNow = async () => {
        setLoading(true);
        setStatus('processing');
        setErrorMsg('');

        // 1. Load Razorpay JS
        const loaded = await loadRazorpayScript();
        if (!loaded) {
            setErrorMsg('Failed to load Razorpay SDK. Check your internet connection.');
            setStatus('error');
            setLoading(false);
            return;
        }

        // 2. Create order on backend
        let orderData;
        try {
            const res = await api.post(`/payment/create-order/${doctorId}`);
            orderData = res.data;
        } catch (err) {
            setErrorMsg(err.response?.data?.message || 'Could not initiate payment. Please try again.');
            setStatus('error');
            setLoading(false);
            return;
        }

        // 3. Open Razorpay Checkout
        const slotDatetime = `${slotDate}T${slotTime}:00`;

        const options = {
            key: orderData.keyId,
            amount: Math.round(parseFloat(orderData.amount) * 100), // in paise
            currency: orderData.currency || 'INR',
            name: 'Hospital Management System',
            description: `Consultation with Dr. ${orderData.doctorName}`,
            order_id: orderData.razorpayOrderId,
            theme: { color: '#7c3aed' },
            prefill: {},
            handler: async (response) => {
                // 4. Verify payment + book appointment on backend
                setStatus('verifying');
                try {
                    const verifyRes = await api.post('/payment/verify-and-book', {
                        razorpayOrderId: response.razorpay_order_id,
                        razorpayPaymentId: response.razorpay_payment_id,
                        razorpaySignature: response.razorpay_signature,
                        doctorId: doctorId,
                        slotDatetime: slotDatetime,
                        notes: notes,
                    });
                    setStatus('done');
                    setLoading(false);
                    onSuccess(verifyRes.data);
                } catch (err) {
                    setErrorMsg(err.response?.data?.message || 'Payment received but booking failed. Please contact support.');
                    setStatus('error');
                    setLoading(false);
                }
            },
            modal: {
                ondismiss: () => {
                    if (status !== 'done') {
                        setStatus('idle');
                        setLoading(false);
                    }
                },
            },
        };

        const rzp = new window.Razorpay(options);
        rzp.on('payment.failed', (response) => {
            setErrorMsg(`Payment failed: ${response.error.description}`);
            setStatus('error');
            setLoading(false);
        });
        rzp.open();
    };

    return (
        <div className="payment-overlay" onClick={(e) => { if (e.target.classList.contains('payment-overlay')) onCancel(); }}>
            <div className="payment-modal">
                {/* Header */}
                <div className="payment-modal-header">
                    <div className="payment-icon-wrap">
                        <svg width="32" height="32" fill="none" viewBox="0 0 24 24" stroke="currentColor" strokeWidth="1.5">
                            <path strokeLinecap="round" strokeLinejoin="round"
                                d="M2.25 8.25h19.5M2.25 9h19.5m-16.5 5.25h6m-6 2.25h3m-3.75 3h15a2.25 2.25 0 002.25-2.25V6.75A2.25 2.25 0 0019.5 4.5h-15a2.25 2.25 0 00-2.25 2.25v10.5A2.25 2.25 0 004.5 19.5z" />
                        </svg>
                    </div>
                    <h2 className="payment-modal-title">Confirm & Pay</h2>
                    <p className="payment-modal-subtitle">Secure online payment powered by Razorpay</p>
                    <button className="payment-close-btn" onClick={onCancel} aria-label="Close">&times;</button>
                </div>

                {/* Booking Summary */}
                <div className="payment-summary">
                    <div className="payment-summary-row">
                        <span className="payment-summary-label">👨‍⚕️ Doctor</span>
                        <span className="payment-summary-value">Dr. {doctorName}</span>
                    </div>
                    <div className="payment-summary-row">
                        <span className="payment-summary-label">🏥 Specialization</span>
                        <span className="payment-summary-value">{specialization || 'General'}</span>
                    </div>
                    <div className="payment-summary-row">
                        <span className="payment-summary-label">📅 Date</span>
                        <span className="payment-summary-value">{slotDate}</span>
                    </div>
                    <div className="payment-summary-row">
                        <span className="payment-summary-label">🕐 Time</span>
                        <span className="payment-summary-value">{slotTime}</span>
                    </div>
                    <div className="payment-divider" />
                    <div className="payment-summary-row payment-total-row">
                        <span className="payment-total-label">Total Amount</span>
                        <span className="payment-total-amount">₹{parseFloat(fee || 0).toFixed(2)}</span>
                    </div>
                </div>

                {/* Status Messages */}
                {status === 'error' && (
                    <div className="payment-alert payment-alert-error">
                        <span>⚠️ {errorMsg}</span>
                    </div>
                )}
                {status === 'verifying' && (
                    <div className="payment-alert payment-alert-info">
                        <div className="payment-spinner-sm" /> Verifying payment & booking your slot…
                    </div>
                )}

                {/* Action Buttons */}
                <div className="payment-actions">
                    <button className="payment-cancel-btn" onClick={onCancel} disabled={loading}>
                        Cancel
                    </button>
                    <button
                        className="payment-pay-btn"
                        onClick={handlePayNow}
                        disabled={loading}
                        id="btn-pay-now"
                    >
                        {loading ? (
                            <><div className="payment-spinner" /> Processing…</>
                        ) : (
                            <><span>🔒</span> Pay ₹{parseFloat(fee || 0).toFixed(2)}</>
                        )}
                    </button>
                </div>

                {/* Trust Badges */}
                <div className="payment-trust">
                    <span>🛡️ 256-bit SSL Encrypted</span>
                    <span>✅ Razorpay Secured</span>
                    <span>🔄 Instant Confirmation</span>
                </div>
            </div>
        </div>
    );
}

export default PaymentModal;
