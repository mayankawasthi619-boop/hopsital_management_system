import { useState, useContext } from 'react';
import { Form, Button, Container, Alert } from 'react-bootstrap';
import { AuthContext } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';

function Login() {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const { login } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        try {
            await login(email, password);
            navigate('/');
        } catch (err) {
            setError(err.response?.data?.message || 'Failed to login');
        }
    };

    return (
        <Container className="auth-wrapper">
            <div className="auth-card glass-card fade-in">
                <div className="text-center mb-5 stagger-1">
                    <h2 className="fw-bolder" style={{fontSize: "2.5rem"}}>Welcome Back</h2>
                    <p className="text-muted">Sign in to continue to CareSync</p>
                </div>
                
                {error && <Alert variant="danger" className="border-0 rounded-3">{error}</Alert>}
                
                <Form onSubmit={handleSubmit} className="stagger-2">
                    <Form.Group className="mb-4">
                        <Form.Label>Email Address</Form.Label>
                        <Form.Control 
                            type="email" 
                            placeholder="name@example.com"
                            value={email} 
                            onChange={(e) => setEmail(e.target.value)} 
                            required 
                        />
                    </Form.Group>
                    <Form.Group className="mb-5">
                        <div className="d-flex justify-content-between">
                            <Form.Label>Password</Form.Label>
                            <a href="#" style={{fontSize: "0.85rem"}}>Forgot password?</a>
                        </div>
                        <Form.Control 
                            type="password" 
                            placeholder="••••••••"
                            value={password} 
                            onChange={(e) => setPassword(e.target.value)} 
                            required 
                        />
                    </Form.Group>
                    <Button variant="primary" type="submit" className="w-100 py-3 mb-4 fs-6">Sign In System</Button>
                </Form>
                
                <div className="text-center stagger-3 text-muted">
                    New to CareSync? <Link to="/register" className="fw-bold">Create an account</Link>
                </div>
            </div>
        </Container>
    );
}
export default Login;
