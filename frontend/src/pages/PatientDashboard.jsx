import { useState, useEffect } from 'react';
import { Container, Table, Badge, Button, Row, Col } from 'react-bootstrap';
import api from '../api/axios';

function PatientDashboard() {
    const [appointments, setAppointments] = useState([]);
    const [profile, setProfile] = useState({});

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        try {
            const apptsRes = await api.get('/appointments/patient');
            setAppointments(apptsRes.data);
            const profRes = await api.get('/patients/profile');
            setProfile(profRes.data);
        } catch (err) {
            console.error(err);
        }
    };

    const handleCancel = async (id) => {
        if (window.confirm('Are you sure you want to cancel this appointment?')) {
            try {
                await api.put(`/appointments/${id}/cancel`);
                loadData();
            } catch (err) {
                alert('Error canceling appointment');
            }
        }
    };

    const handleDownloadBill = async (id) => {
        try {
            const res = await api.get(`/billing/${id}/download`, { responseType: 'blob' });
            const url = window.URL.createObjectURL(new Blob([res.data]));
            const link = document.createElement('a');
            link.href = url;
            link.setAttribute('download', `invoice_${id}.pdf`);
            document.body.appendChild(link);
            link.click();
            link.remove();
        } catch (err) {
            console.error('Download failed', err);
        }
    };

    return (
        <Container className="mt-5 fade-in">
            <Row className="mb-4 align-items-center stagger-1">
                <Col>
                    <h2 className="dashboard-header m-0">My Dashboard</h2>
                    <p className="text-muted mt-2">
                        Welcome back, <strong className="text-white">{profile.name}</strong> • {profile.email} • {profile.phone || 'No phone set'}
                    </p>
                </Col>
            </Row>

            <div className="glass-card p-4 stagger-2">
                <h4 className="mb-4">My Appointments</h4>
                <div className="table-responsive">
                    <Table hover className="align-middle border-0 m-0">
                        <thead>
                            <tr>
                                <th>Date & Time</th>
                                <th>Doctor</th>
                                <th>Specialization</th>
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
                                        <div className="fw-bold">Dr. {appt.doctor?.name}</div>
                                    </td>
                                    <td className="text-muted">{appt.doctor?.specialization}</td>
                                    <td>
                                        <Badge bg={appt.status === 'COMPLETED' ? 'success' : appt.status === 'CANCELLED' ? 'danger' : 'warning'}>
                                            {appt.status}
                                        </Badge>
                                    </td>
                                    <td className="text-end">
                                        {(appt.status === 'PENDING' || appt.status === 'CONFIRMED') && (
                                            <Button variant="danger" size="sm" onClick={() => handleCancel(appt.id)}>Cancel</Button>
                                        )}
                                        {appt.status === 'COMPLETED' && (
                                            <Button variant="outline-primary" size="sm" onClick={() => handleDownloadBill(appt.id)}>Download Bill</Button>
                                        )}
                                    </td>
                                </tr>
                            ))}
                            {appointments.length === 0 && (
                                <tr>
                                    <td colSpan="5" className="text-center py-5 text-muted">
                                        No appointments found. <br/> 
                                        <Button href="/book" variant="primary" size="sm" className="mt-3">Book an Appointment</Button>
                                    </td>
                                </tr>
                            )}
                        </tbody>
                    </Table>
                </div>
            </div>
        </Container>
    );
}

export default PatientDashboard;
