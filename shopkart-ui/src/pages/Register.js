import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './css/Register.css';

function Register() {

    const [name, setName] = useState('');
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirm, setConfirm] = useState('');
    const [errors, setErrors] = useState({});
    const [serverError, setServerError] = useState('');
    const [loading, setLoading] = useState(false);
    const navigate = useNavigate();

    const validate = () => {
        const newErrors = {};
        if (!name) newErrors.name = 'Name is required';
        else if (name.length < 2) newErrors.name = 'Name must be at least 2 characters';
        if (!email) newErrors.email = 'Email is required';
        else if (!email.includes('@')) newErrors.email = 'Enter a valid email';
        if (!password) newErrors.password = 'Password is required';
        else if (password.length < 6) newErrors.password = 'Password must be at least 6 characters';
        if (!confirm) newErrors.confirm = 'Please confirm your password';
        else if (password !== confirm) newErrors.confirm = 'Passwords do not match';
        return newErrors;
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setServerError('');

        const foundErrors = validate();
        if (Object.keys(foundErrors).length > 0) {
            setErrors(foundErrors);
            return;
        }

        setLoading(true);
        try {
            const response = await fetch('http://localhost:8080/api/auth/register', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name, email, password })
            });

            const data = await response.json();

            if (response.ok) {
                localStorage.setItem('token', data.token);
                localStorage.setItem('userEmail', data.email);
                localStorage.setItem('userName', data.name);
                localStorage.setItem('userRole', data.role);
                navigate('/products');
            } else {
                setServerError(data.message || 'Registration failed.');
            }

        } catch (error) {
            setServerError('Cannot connect to server. Make sure clothwear service is running on port 8080.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="register-container">

            <h2 className="register-title">ShopKart Register</h2>

            {serverError && (
                <div className="register-server-error">
                    {serverError}
                </div>
            )}

            <form onSubmit={handleSubmit}>

                <div className="register-field">
                    <label className="register-label">Full Name</label>
                    <input
                        type="text"
                        className="register-input"
                        value={name}
                        onChange={(e) => setName(e.target.value)}
                        placeholder="Chaitanya"
                    />
                    {errors.name && <p className="register-field-error">{errors.name}</p>}
                </div>

                <div className="register-field">
                    <label className="register-label">Email</label>
                    <input
                        type="text"
                        className="register-input"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="chaitanya@test.com"
                    />
                    {errors.email && <p className="register-field-error">{errors.email}</p>}
                </div>

                <div className="register-field">
                    <label className="register-label">Password</label>
                    <input
                        type="password"
                        className="register-input"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Min 6 characters"
                    />
                    {errors.password && <p className="register-field-error">{errors.password}</p>}
                </div>

                <div className="register-field">
                    <label className="register-label">Confirm Password</label>
                    <input
                        type="password"
                        className="register-input"
                        value={confirm}
                        onChange={(e) => setConfirm(e.target.value)}
                        placeholder="Repeat your password"
                    />
                    {errors.confirm && <p className="register-field-error">{errors.confirm}</p>}
                </div>

                <button
                    type="submit"
                    className="register-btn"
                    disabled={loading}
                >
                    {loading ? 'Creating account...' : 'Register'}
                </button>

            </form>

            <p className="register-switch">
                Already have an account?{' '}
                <span
                    className="register-link"
                    onClick={() => navigate('/login')}
                >
                    Login here
                </span>
            </p>

        </div>
    );
}

export default Register;