import { useState, useEffect } from 'react';
import { Container, Table, Badge, Button, Modal, Form } from 'react-bootstrap';
import api from '../api/axios';

function DoctorDashboard() {
    const [appointments, setAppointments] = useState([]);
    const [showModal, setShowModal] = useState(false);
    const [activeAppointmentId, setActiveAppointmentId] = useState(null);
    const [diagnosis, setDiagnosis] = useState('');
    const [medicines, setMedicines] = useState('');
    const [instructions, setInstructions] = useState('');

    useEffect(() => {
        loadAppointments();
    }, []);

    const loadAppointments = async () => {
        try {
            const res = await api.get('/appointments/doctor');
            setAppointments(res.data);
        } catch(err) { console.error(err); }
    };

    const handleComplete = async (id) => {
        if(window.confirm('Mark this appointment as completed?')) {
            try {
                await api.put(`/appointments/${id}/complete`);
                loadAppointments();
            } catch(e) { alert('Failed to complete'); }
        }
    };

    const openPrescriptionModal = (id) => {
        setActiveAppointmentId(id);
        setDiagnosis(''); setMedicines(''); setInstructions('');
        setShowModal(true);
    };

    const submitPrescription = async (e) => {
        e.preventDefault();
        try {
            await api.post(`/appointments/${activeAppointmentId}/prescription`, {
                diagnosis, medicines, instructions
            });
            setShowModal(false);
            alert('Prescription appended successfully.');
        } catch(e) { alert('Failed to save prescription'); }
    };

    return (
        <Container className="mt-5 fade-in">
            <h2 className="dashboard-header text-center mb-5 stagger-1">Doctor Portal</h2>
            
            <div className="glass-card p-4 stagger-2">
                <Table hover responsive className="align-middle border-0 m-0">
                    <thead>
                        <tr>
                            <th>Date & Time</th>
                            <th>Patient Name</th>
                            <th>Notes</th>
                            <th>Status</th>
                            <th className="text-end">Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        {appointments.map(appt => (
                            <tr key={appt.id}>
                                <td>
                                    <div className="fw-bold">{new Date(appt.slotDatetime).toLocaleDateString()}</div>
                                    <div className="text-muted small">{new Date(appt.slotDatetime).toLocaleTimeString([], {hour: '2-digit', minute:'2-digit'})}</div>
                                </td>
                                <td>
                                    <div className="fw-bold">{appt.patient?.name}</div>
                                </td>
                                <td>
                                    <span className="text-muted d-inline-block text-truncate" style={{maxWidth: '150px'}} title={appt.notes}>
                                        {appt.notes || '--'}
                                    </span>
                                </td>
                                <td>
                                    <Badge bg={appt.status === 'COMPLETED' ? 'success' : appt.status === 'CANCELLED' ? 'danger' : 'primary'}>
                                        {appt.status}
                                    </Badge>
                                </td>
                                <td className="text-end">
                                    {(appt.status === 'PENDING' || appt.status === 'CONFIRMED') && (
                                        <Button variant="outline-primary" size="sm" onClick={() => handleComplete(appt.id)}>Complete</Button>
                                    )}
                                    {appt.status === 'COMPLETED' && (
                                        <Button variant="primary" size="sm" className="ms-2" onClick={() => openPrescriptionModal(appt.id)}>+ Prescription</Button>
                                    )}
                                </td>
                            </tr>
                        ))}
                        {appointments.length === 0 && (
                            <tr><td colSpan="5" className="text-center py-5 text-muted">No appointments found cleanly matching your schedule.</td></tr>
                        )}
                    </tbody>
                </Table>
            </div>

            <Modal show={showModal} onHide={() => setShowModal(false)} centered>
                <div className="modal-content">
                    <Modal.Header closeButton className="border-bottom-0 pb-0">
                        <Modal.Title className="fw-bold text-white">Add Prescription</Modal.Title>
                    </Modal.Header>
                    <Modal.Body className="pt-2">
                        <p className="text-muted mb-4 small">Dispensary form bindings are formally secured onto Patient ID.</p>
                        <Form onSubmit={submitPrescription}>
                            <Form.Group className="mb-3">
                                <Form.Label>Diagnosis Details</Form.Label>
                                <Form.Control as="textarea" rows={2} required value={diagnosis} onChange={e=>setDiagnosis(e.target.value)} placeholder="Summary of condition..."/>
                            </Form.Group>
                            <Form.Group className="mb-3">
                                <Form.Label>Prescribed Medicines</Form.Label>
                                <Form.Control as="textarea" rows={3} required value={medicines} onChange={e=>setMedicines(e.target.value)} placeholder="E.g., Ibuprofen 400mg x 2"/>
                            </Form.Group>
                            <Form.Group className="mb-4">
                                <Form.Label>Special Instructions</Form.Label>
                                <Form.Control type="text" value={instructions} onChange={e=>setInstructions(e.target.value)} placeholder="Take after meals"/>
                            </Form.Group>
                            <Button variant="primary" type="submit" className="w-100">Save Secure Record</Button>
                        </Form>
                    </Modal.Body>
                </div>
            </Modal>
        </Container>
    );
}

export default DoctorDashboard;
