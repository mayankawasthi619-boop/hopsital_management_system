import { useState, useContext } from 'react';
import { Form, Button, Container, Alert, Row, Col } from 'react-bootstrap';
import { AuthContext } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';

function Register() {
    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [role, setRole] = useState('PATIENT');
    const [error, setError] = useState('');
    const { register } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await register(name, email, password, role);
            navigate('/');
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to register');
        }
    };

    return (
        <Container className="auth-wrapper">
            <div className="auth-card glass-card fade-in p-5" style={{maxWidth: "600px"}}>
                <div className="text-center mb-5 stagger-1">
                    <h2 className="fw-bolder" style={{fontSize: "2.5rem"}}>Join CareSync</h2>
                    <p className="text-muted">Create an account to manage your healthcare</p>
                </div>
                
                {error && <Alert variant="danger" className="border-0 rounded-3">{error}</Alert>}
                
                <Form onSubmit={handleSubmit} className="stagger-2">
                    <Row>
                        <Col md={12}>
                            <Form.Group className="mb-4">
                                <Form.Label>Full Legal Name</Form.Label>
                                <Form.Control 
                                    type="text" 
                                    placeholder="John Doe"
                                    value={name} 
                                    onChange={(e) => setName(e.target.value)} 
                                    required 
                                />
                            </Form.Group>
                        </Col>
                        <Col md={12}>
                            <Form.Group className="mb-4">
                                <Form.Label>Email Address</Form.Label>
                                <Form.Control 
                                    type="email" 
                                    placeholder="john@example.com"
                                    value={email} 
                                    onChange={(e) => setEmail(e.target.value)} 
                                    required 
                                />
                            </Form.Group>
                        </Col>
                        <Col md={6}>
                            <Form.Group className="mb-4">
                                <Form.Label>Password</Form.Label>
                                <Form.Control 
                                    type="password" 
                                    placeholder="Select a secure password"
                                    value={password} 
                                    onChange={(e) => setPassword(e.target.value)} 
                                    required 
                                />
                            </Form.Group>
                        </Col>
                        <Col md={6}>
                            <Form.Group className="mb-5">
                                <Form.Label>Account Type</Form.Label>
                                <Form.Select value={role} onChange={(e) => setRole(e.target.value)}>
                                    <option value="PATIENT">Patient (Standard)</option>
                                    <option value="DOCTOR">Doctor / Specialist</option>
                                </Form.Select>
                            </Form.Group>
                        </Col>
                    </Row>
                    
                    <Button variant="primary" type="submit" className="w-100 py-3 mb-4 fs-6">Complete Registration</Button>
                </Form>
                
                <div className="text-center stagger-3 text-muted">
                    Already have an account? <Link to="/login" className="fw-bold">Sign in here</Link>
                </div>
            </div>
        </Container>
    );
}
export default Register;
