import { useState, useEffect } from 'react';
import { Container, Form, Button, Row, Col } from 'react-bootstrap';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

function BookAppointment() {
    const [doctors, setDoctors] = useState([]);
    const [selectedDoctor, setSelectedDoctor] = useState('');
    const [slotDate, setSlotDate] = useState('');
    const [slotTime, setSlotTime] = useState('');
    const [notes, setNotes] = useState('');
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

    const handleSubmit = async (e) => {
        e.preventDefault();
        
        const dtStr = `${slotDate}T${slotTime}:00`;

        try {
            await api.post('/appointments/book', {
                doctorId: selectedDoctor,
                slotDatetime: dtStr,
                notes: notes
            });
            alert('Appointment successfully booked! A confirmation email has been dispatched.');
            navigate('/patient');
        } catch (err) {
            alert(err.response?.data?.message || 'Error booking slot. Conflict may exist.');
        }
    };

    return (
        <Container className="auth-wrapper py-5 fade-in">
            <div className="glass-card p-5 w-100" style={{maxWidth: "800px"}}>
                <div className="text-center mb-5 stagger-1">
                    <h2 className="dashboard-header m-0" style={{fontSize: "2.5rem"}}>Book Appointment</h2>
                    <p className="text-muted mt-2">Secure your slot with our leading medical specialists</p>
                </div>

                <Form onSubmit={handleSubmit} className="stagger-2">
                    <Row>
                        <Col md={12}>
                            <Form.Group className="mb-4">
                                <Form.Label>Select Specialist</Form.Label>
                                <Form.Select value={selectedDoctor} onChange={e=>setSelectedDoctor(e.target.value)} required>
                                    <option value="">-- Choose a Doctor --</option>
                                    {doctors.map(doc => (
                                        <option key={doc.id} value={doc.id}>
                                            {doc.name} - {doc.specialization || 'General'} (₹{doc.consultationFee || 0})
                                        </option>
                                    ))}
                                </Form.Select>
                            </Form.Group>
                        </Col>
                        
                        <Col md={6}>
                            <Form.Group className="mb-4">
                                <Form.Label>Preferred Date</Form.Label>
                                <Form.Control type="date" value={slotDate} onChange={e=>setSlotDate(e.target.value)} required />
                            </Form.Group>
                        </Col>
                        <Col md={6}>
                            <Form.Group className="mb-4">
                                <Form.Label>Preferred Time Slot</Form.Label>
                                <Form.Select value={slotTime} onChange={e=>setSlotTime(e.target.value)} required>
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
                        <Form.Control as="textarea" rows={4} value={notes} onChange={e=>setNotes(e.target.value)} placeholder="Please describe your symptoms or reason for visit briefly..." />
                    </Form.Group>
                    
                    <Button variant="primary" type="submit" className="w-100 py-3 fs-6">Confirm Healthcare Booking</Button>
                </Form>
            </div>
        </Container>
    );
}

export default BookAppointment;
