import { useState, useEffect } from 'react';
import { Container, Form, Button, Row, Col } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';
import PaymentModal from '../components/PaymentModal';

function BookAppointment() {
    const [doctors, setDoctors] = useState([]);
    const [selectedDoctor, setSelectedDoctor] = useState('');
    const [selectedDoctorData, setSelectedDoctorData] = useState(null);
    const [slotDate, setSlotDate] = useState('');
    const [slotTime, setSlotTime] = useState('');
    const [notes, setNotes] = useState('');
    const [showPayment, setShowPayment] = useState(false);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchDoctors = async () => {
            try {
                const res = await api.get('/doctors');
                setDoctors(res.data);
            } catch (err) {
                console.error("Error fetching doctors", err);
            }
        };
        fetchDoctors();
    }, []);

    // When doctor selection changes, update the selected doctor data object
    const handleDoctorChange = (e) => {
        const id = e.target.value;
        setSelectedDoctor(id);
        if (id) {
            const doc = doctors.find(d => String(d.id) === String(id));
            setSelectedDoctorData(doc || null);
        } else {
            setSelectedDoctorData(null);
        }
    };

    const handleSubmit = (e) => {
        e.preventDefault();
        if (!selectedDoctor || !slotDate || !slotTime) {
            alert('Please fill in all required fields.');
            return;
        }
        // Show the payment modal instead of booking directly
        setShowPayment(true);
    };

    const handlePaymentSuccess = (appointmentDTO) => {
        setShowPayment(false);
        alert(`✅ Payment successful! Your appointment #${appointmentDTO.id} is CONFIRMED.\nA confirmation email has been sent to you.`);
        navigate('/patient');
    };

    const handlePaymentCancel = () => {
        setShowPayment(false);
    };

    // Today's date in YYYY-MM-DD format for min attribute
    const today = new Date().toISOString().split('T')[0];

    return (
        <>
            <Container className="auth-wrapper py-5 fade-in">
                <div className="glass-card p-5 w-100" style={{ maxWidth: "800px" }}>
                    <div className="text-center mb-5 stagger-1">
                        <h2 className="dashboard-header m-0" style={{ fontSize: "2.5rem" }}>Book Appointment</h2>
                        <p className="text-muted mt-2">Secure your slot with our leading medical specialists</p>
                    </div>

                    <Form onSubmit={handleSubmit} className="stagger-2">
                        <Row>
                            <Col md={12}>
                                <Form.Group className="mb-4">
                                    <Form.Label>Select Specialist</Form.Label>
                                    <Form.Select value={selectedDoctor} onChange={handleDoctorChange} required>
                                        <option value="">-- Choose a Doctor --</option>
                                        {doctors.map(doc => (
                                            <option key={doc.id} value={doc.id}>
                                                {doc.name} - {doc.specialization || 'General'} (₹{doc.consultationFee || 0})
                                            </option>
                                        ))}
                                    </Form.Select>
                                </Form.Group>
                            </Col>

                            {/* Fee preview card */}
                            {selectedDoctorData && (
                                <Col md={12}>
                                    <div className="fee-preview-card mb-4">
                                        <div className="fee-preview-left">
                                            <div className="fee-preview-avatar">
                                                {selectedDoctorData.name?.charAt(0).toUpperCase()}
                                            </div>
                                            <div>
                                                <div className="fee-preview-name">Dr. {selectedDoctorData.name}</div>
                                                <div className="fee-preview-spec">{selectedDoctorData.specialization || 'General Physician'}</div>
                                            </div>
                                        </div>
                                        <div className="fee-preview-right">
                                            <div className="fee-preview-label">Consultation Fee</div>
                                            <div className="fee-preview-amount">₹{selectedDoctorData.consultationFee || 0}</div>
                                        </div>
                                    </div>
                                </Col>
                            )}

                            <Col md={6}>
                                <Form.Group className="mb-4">
                                    <Form.Label>Preferred Date</Form.Label>
                                    <Form.Control
                                        type="date"
                                        value={slotDate}
                                        min={today}
                                        onChange={e => setSlotDate(e.target.value)}
                                        required
                                    />
                                </Form.Group>
                            </Col>
                            <Col md={6}>
                                <Form.Group className="mb-4">
                                    <Form.Label>Preferred Time Slot</Form.Label>
                                    <Form.Select value={slotTime} onChange={e => setSlotTime(e.target.value)} required>
                                        <option value="">-- Select Time --</option>
                                        <option value="09:00">09:00 AM</option>
                                        <option value="10:00">10:00 AM</option>
                                        <option value="11:00">11:00 AM</option>
                                        <option value="13:00">01:00 PM</option>
                                        <option value="14:00">02:00 PM</option>
                                        <option value="15:00">03:00 PM</option>
                                        <option value="16:00">04:00 PM</option>
                                        <option value="17:00">05:00 PM</option>
                                    </Form.Select>
                                </Form.Group>
                            </Col>
                        </Row>

                        <Form.Group className="mb-5">
                            <Form.Label>Brief Description / Symptoms</Form.Label>
                            <Form.Control
                                as="textarea"
                                rows={4}
                                value={notes}
                                onChange={e => setNotes(e.target.value)}
                                placeholder="Please describe your symptoms or reason for visit briefly..."
                            />
                        </Form.Group>

                        {/* Payment info banner */}
                        <div className="payment-info-banner mb-4">
                            <span>🔒</span>
                            <span>Online payment via <strong>Razorpay</strong> — pay securely using UPI, Card, Net Banking or Wallet</span>
                        </div>

                        <Button variant="primary" type="submit" className="w-100 py-3 fs-6" id="btn-proceed-to-pay">
                            🛒 Proceed to Pay & Book
                        </Button>
                    </Form>
                </div>
            </Container>

            {/* Razorpay Payment Modal */}
            {showPayment && (
                <PaymentModal
                    doctorId={selectedDoctor}
                    doctorName={selectedDoctorData?.name}
                    specialization={selectedDoctorData?.specialization}
                    fee={selectedDoctorData?.consultationFee}
                    slotDate={slotDate}
                    slotTime={slotTime}
                    notes={notes}
                    onSuccess={handlePaymentSuccess}
                    onCancel={handlePaymentCancel}
                />
            )}
        </>
    );
}

export default BookAppointment;
