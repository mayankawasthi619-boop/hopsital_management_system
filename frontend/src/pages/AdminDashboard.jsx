import { useState, useEffect } from 'react';
import { Container, Row, Col } from 'react-bootstrap';
import api from '../api/axios';

function AdminDashboard() {
    const [stats, setStats] = useState({
        totalPatients: 0,
        totalDoctors: 0,
        appointmentsToday: 0,
        availableDoctors: 0
    });

    useEffect(() => {
        const fetchStats = async () => {
            try {
                const res = await api.get('/admin/dashboard/stats');
                setStats(res.data);
            } catch (err) {
                console.error("Failed fetching admin stats", err);
            }
        };
        fetchStats();
    }, []);

    return (
        <Container className="mt-5 fade-in">
            <h2 className="dashboard-header mb-5 text-center">System Overview</h2>
            
            <Row className="g-4 stagger-1">
                <Col lg={3} md={6}>
                    <div className="glass-card stat-card">
                        <div className="stat-title">Total Patients</div>
                        <div className="stat-value">{stats.totalPatients}</div>
                    </div>
                </Col>
                <Col lg={3} md={6}>
                    <div className="glass-card stat-card stagger-2">
                        <div className="stat-title">Registered Doctors</div>
                        <div className="stat-value">{stats.totalDoctors}</div>
                    </div>
                </Col>
                <Col lg={3} md={6}>
                    <div className="glass-card stat-card stagger-3">
                        <div className="stat-title">Today's Appointments</div>
                        <div className="stat-value" style={{background: 'linear-gradient(45deg, #ff007a, #ff4b2b)', WebkitBackgroundClip: 'text'}}>{stats.appointmentsToday}</div>
                    </div>
                </Col>
                <Col lg={3} md={6}>
                    <div className="glass-card stat-card stagger-3">
                        <div className="stat-title">Available Doctors</div>
                        <div className="stat-value" style={{background: 'linear-gradient(45deg, #00f2fe, #4facfe)', WebkitBackgroundClip: 'text'}}>{stats.availableDoctors}</div>
                    </div>
                </Col>
            </Row>

            <Row className="mt-5 pt-3 stagger-2">
                <Col md={12}>
                    <div className="glass-card p-4">
                        <h4 className="mb-4 fw-bold">Recent System Activity (Placeholder)</h4>
                        <p className="text-muted">Charts and dynamic visual graphs would attach perfectly into this spacious neon-styled card frame.</p>
                    </div>
                </Col>
            </Row>
        </Container>
    );
}

export default AdminDashboard;
