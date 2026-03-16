import { useContext } from 'react';
import { Navbar, Nav, Container, Button } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { AuthContext } from '../context/AuthContext';

function TopNav() {
    const { user, logout } = useContext(AuthContext);
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <Navbar expand="lg" className="navbar-custom py-3 sticky-top">
            <Container>
                <Navbar.Brand as={Link} to="/">CareSync</Navbar.Brand>
                <Navbar.Toggle aria-controls="basic-navbar-nav" className="border-0 shadow-none" />
                <Navbar.Collapse id="basic-navbar-nav">
                    <Nav className="me-auto ms-4">
                        {user && user.role === 'ADMIN' && <Nav.Link as={Link} to="/admin" className="nav-link">Dashboard</Nav.Link>}
                        {user && user.role === 'DOCTOR' && <Nav.Link as={Link} to="/doctor" className="nav-link">Doctor Board</Nav.Link>}
                        {user && user.role === 'PATIENT' && <Nav.Link as={Link} to="/patient" className="nav-link">My Profile</Nav.Link>}
                        {user && user.role === 'PATIENT' && <Nav.Link as={Link} to="/book" className="nav-link">Book Appointment</Nav.Link>}
                    </Nav>
                    <Nav className="align-items-center">
                        {user ? (
                            <>
                                <Navbar.Text className="me-4 text-white font-weight-bold">
                                    <span style={{opacity: 0.7, marginRight: "5px"}}>Logged in as</span>
                                    {user.name} 
                                    <span className="badge bg-primary ms-2" style={{color: "#000"}}>{user.role}</span>
                                </Navbar.Text>
                                <Button variant="outline-primary" size="sm" onClick={handleLogout} style={{borderRadius: "8px"}}>Logout</Button>
                            </>
                        ) : (
                            <>
                                <Nav.Link as={Link} to="/login" className="me-3 nav-link">Sign In</Nav.Link>
                                <Button as={Link} to="/register" variant="primary">Create Account</Button>
                            </>
                        )}
                    </Nav>
                </Navbar.Collapse>
            </Container>
        </Navbar>
    );
}

export default TopNav;
