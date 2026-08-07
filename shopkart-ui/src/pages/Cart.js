import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './css/Cart.css';

function Cart() {

    const [cart, setCart] = useState(null);
    const [loading, setLoading] = useState(true);
    const [serverError, setServerError] = useState('');
    const navigate = useNavigate();

    const userEmail = localStorage.getItem('userEmail');
    const token = localStorage.getItem('token');

    // Check if logged in
    useEffect(() => {
        if (!token) {
            navigate('/login');
            return;
        }
        fetchCart();
    }, []);

    // Fetch cart from Cart Service
    const fetchCart = async () => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8082/api/cart/${userEmail}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                }
            });

            if (response.ok) {
                const data = await response.json();
                setCart(data);
            } else {
                setServerError('Failed to load cart.');
            }

        } catch (error) {
            setServerError('Cannot connect to Cart Service on port 8082.');
        } finally {
            setLoading(false);
        }
    };

    // Loading state
    if (loading) {
        return <div className="cart-loading">Loading your cart...</div>;
    }

    return (
        <div className="cart-page">

            {/* Navbar */}
            <div className="cart-navbar">
                <h2 className="cart-navbar-title">ShopKart — My Cart</h2>
                <div className="cart-navbar-right">
                    <button className="cart-nav-btn" onClick={() => navigate('/products')}>
                        ← Back to Products
                    </button>
                    <button className="cart-nav-btn-outline" onClick={() => navigate('/orders')}>
                        My Orders
                    </button>
                </div>
            </div>

            {/* Server error */}
            {serverError && (
                <div className="cart-server-error">{serverError}</div>
            )}

            {/* Empty cart */}
            {!cart || !cart.items || cart.items.length === 0 ? (
                <div className="cart-empty">
                    <p>Your cart is empty.</p>
                    <button className="cart-shop-btn" onClick={() => navigate('/products')}>
                        Shop Now
                    </button>
                </div>
            ) : (
                <div className="cart-content">

                    {/* Cart Items List */}
                    <div className="cart-items">
                        {cart.items.map(item => (
                            <div key={item.cartItemId} className="cart-item">

                                <img
                                    src={item.imageUrl}
                                    alt={item.title}
                                    className="cart-item-img"
                                    onError={(e) => e.target.style.display = 'none'}
                                />

                                <div className="cart-item-info">
                                    <p className="cart-item-brand">{item.brand}</p>
                                    <p className="cart-item-title">{item.title}</p>
                                    <p className="cart-item-price">
                                        ${item.price} × {item.quantity} = <strong>${item.lineTotal}</strong>
                                    </p>
                                </div>

                                {/* NOTE TO AKSHAYA: Add remove button here */}

                            </div>
                        ))}
                    </div>

                    {/* NOTE TO AKSHAYA: Add cart summary and checkout button here */}

                </div>
            )}
        </div>
    );
}

export default Cart;