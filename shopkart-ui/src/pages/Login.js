import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

function Login() {

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [errors, setErrors] = useState({});
    const navigate = useNavigate();

    const validate = () => {
        const newErrors = {};

        if (!email) {
            newErrors.email = 'Email is required';
        } else if (!email.includes('@')) {
            newErrors.email = 'Enter a valid email';
        }

        if (!password) {
            newErrors.password = 'Password is required';
        } else if (password.length < 6) {
            newErrors.password = 'Password must be at least 6 characters';
        }

        return newErrors;
    };

    const handleSubmit = (e) => {
        e.preventDefault();

        const foundErrors = validate();

        if (Object.keys(foundErrors).length > 0) {
            setErrors(foundErrors);
            return;
        }

        alert(`Login successful!\nEmail: ${email}`);
    };

    return (
        <div style={{ maxWidth: '400px', margin: '100px auto', padding: '2rem', border: '1px solid #ddd', borderRadius: '10px' }}>

            <h2>ShopKart Login</h2>

            <form onSubmit={handleSubmit}>

                <div style={{ marginBottom: '1rem' }}>
                    <label>Email</label><br />
                    <input
                        type="text"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        placeholder="Enter your email"
                        style={{ width: '100%', padding: '0.5rem', marginTop: '0.25rem' }}
                    />
                    {errors.email && (
                        <p style={{ color: 'red', fontSize: '0.8rem', margin: '0.25rem 0 0' }}>
                            {errors.email}
                        </p>
                    )}
                </div>

                <div style={{ marginBottom: '1rem' }}>
                    <label>Password</label><br />
                    <input
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        placeholder="Enter your password"
                        style={{ width: '100%', padding: '0.5rem', marginTop: '0.25rem' }}
                    />
                    {errors.password && (
                        <p style={{ color: 'red', fontSize: '0.8rem', margin: '0.25rem 0 0' }}>
                            {errors.password}
                        </p>
                    )}
                </div>

                <button
                    type="submit"
                    style={{ width: '100%', padding: '0.75rem', background: '#FF6B00', color: '#fff', border: 'none', borderRadius: '8px', cursor: 'pointer' }}
                >
                    Login
                </button>

            </form>

            <p style={{ textAlign: 'center', marginTop: '1rem', fontSize: '0.9rem' }}>
                Don't have an account?{' '}
                <span
                    onClick={() => navigate('/register')}
                    style={{ color: '#FF6B00', cursor: 'pointer', fontWeight: '600' }}
                >
                    Register here
                </span>
            </p>

        </div>
    );
}

export default Login;