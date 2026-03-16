import { useContext } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import TopNav from './components/Navbar';
import Login from './pages/Login';
import Register from './pages/Register';
import PatientDashboard from './pages/PatientDashboard';
import DoctorDashboard from './pages/DoctorDashboard';
import AdminDashboard from './pages/AdminDashboard';
import BookAppointment from './pages/BookAppointment';
import { AuthContext } from './context/AuthContext';

function App() {
    const { user, loading } = useContext(AuthContext);

    if (loading) return <div className="text-center mt-5">Loading...</div>;

    const ProtectedRoute = ({ children, allowedRoles }) => {
        if (!user) return <Navigate to="/login" replace />;
        if (allowedRoles && !allowedRoles.includes(user.role)) return <Navigate to="/" replace />;
        return children;
    };

    return (
        <div>
            <TopNav />
            <Routes>
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                
                <Route path="/patient" element={
                    <ProtectedRoute allowedRoles={['PATIENT']}><PatientDashboard /></ProtectedRoute>
                } />

                <Route path="/book" element={
                    <ProtectedRoute allowedRoles={['PATIENT']}><BookAppointment /></ProtectedRoute>
                } />
                
                <Route path="/doctor" element={
                    <ProtectedRoute allowedRoles={['DOCTOR']}><DoctorDashboard /></ProtectedRoute>
                } />

                <Route path="/admin" element={
                    <ProtectedRoute allowedRoles={['ADMIN']}><AdminDashboard /></ProtectedRoute>
                } />

                <Route path="/" element={
                    user ? (
                        user.role === 'ADMIN' ? <Navigate to="/admin" /> :
                        user.role === 'DOCTOR' ? <Navigate to="/doctor" /> :
                        <Navigate to="/patient" />
                    ) : <Navigate to="/login" />
                } />
            </Routes>
        </div>
    );
}

export default App;
