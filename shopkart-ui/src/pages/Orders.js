import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './css/Orders.css';

function Orders() {

    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
    const [placing, setPlacing] = useState(false);
    const [serverError, setServerError] = useState('');
    const [message, setMessage] = useState('');
    const [form, setForm] = useState({
        shippingAddress: '',
        shippingCity: '',
        shippingState: ''
    });
    const navigate = useNavigate();

    const userEmail = localStorage.getItem('userEmail');
    const token = localStorage.getItem('token');

    useEffect(() => {
        if (!token) {
            navigate('/login');
            return;
        }
        fetchOrders();
    }, []);

    const fetchOrders = async () => {
        setLoading(true);
        try {
            const response = await fetch(`http://localhost:8083/api/orders/user/${userEmail}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                }
            });

            if (response.ok) {
                const data = await response.json();
                setOrders(data);
            } else {
                setServerError('Failed to load orders.');
            }
        } catch (error) {
            setServerError('Cannot connect to Order Service on port 8083.');
        } finally {
            setLoading(false);
        }
    };

    // Place order — fetches cart items first then calls Order Service
    const handlePlaceOrder = async (e) => {
        e.preventDefault();
        setMessage('');
        setServerError('');

        if (!form.shippingAddress || !form.shippingCity) {
            setServerError('Please fill in shipping address and city.');
            return;
        }

        setPlacing(true);
        try {
            // Step 1 — Get cart items from Cart Service
            const cartResponse = await fetch(`http://localhost:8082/api/cart/${userEmail}`, {
                headers: { 'Authorization': 'Bearer ' + token }
            });

            const cartData = await cartResponse.json();

            if (!cartData.items || cartData.items.length === 0) {
                setServerError('Your cart is empty. Add items before placing an order.');
                setPlacing(false);
                return;
            }

            // Step 2 — Place order via Order Service
            const orderResponse = await fetch('http://localhost:8083/api/orders/place', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                },
                body: JSON.stringify({
                    userEmail,
                    shippingAddress: form.shippingAddress,
                    shippingCity: form.shippingCity,
                    shippingState: form.shippingState,
                    items: cartData.items.map(i => ({
                        productId: i.productId,
                        quantity: i.quantity
                    }))
                })
            });

            const orderData = await orderResponse.json();

            if (orderResponse.ok) {
                // Step 3 — Clear the cart
                await fetch(`http://localhost:8082/api/cart/${userEmail}/clear`, {
                    method: 'DELETE',
                    headers: { 'Authorization': 'Bearer ' + token }
                });

                setMessage(`Order placed! Order number: ${orderData.orderNumber}`);
                setForm({ shippingAddress: '', shippingCity: '', shippingState: '' });
                fetchOrders(); // Refresh order list
            } else {
                setServerError(orderData.message || 'Failed to place order.');
            }

        } catch (error) {
            setServerError('Cannot connect to services. Make sure all services are running.');
        } finally {
            setPlacing(false);
        }
    };

    const getStatusColor = (status) => {
        switch (status) {
            case 'PLACED': return '#F59E0B';
            case 'CONFIRMED': return '#3B82F6';
            case 'SHIPPED': return '#8B5CF6';
            case 'DELIVERED': return '#10B981';
            case 'CANCELLED': return '#EF4444';
            default: return '#888';
        }
    };

    if (loading) {
        return <div className="orders-loading">Loading your orders...</div>;
    }

    return (
        <div className="orders-page">

            {/* Navbar */}
            <div className="orders-navbar">
                <h2 className="orders-navbar-title">ShopKart — My Orders</h2>
                <div className="orders-navbar-right">
                    <button className="orders-nav-btn" onClick={() => navigate('/cart')}>
                        ← Back to Cart
                    </button>
                    <button className="orders-nav-btn-outline" onClick={() => navigate('/products')}>
                        Products
                    </button>
                </div>
            </div>

            {/* Server error */}
            {serverError && (
                <div className="orders-server-error">{serverError}</div>
            )}

            {/* Success message */}
            {message && (
                <div className="orders-success">{message}</div>
            )}

            {/* Place Order Form */}
            <div className="place-order-form">
                <h3 className="orders-section-title">Place New Order</h3>
                <p className="orders-subtitle">
                    Items will be taken from your cart automatically
                </p>

                <form onSubmit={handlePlaceOrder}>
                    <div className="orders-field">
                        <label className="orders-label">Shipping Address</label>
                        <input
                            className="orders-input"
                            type="text"
                            placeholder="123 Main Street"
                            value={form.shippingAddress}
                            onChange={(e) => setForm({ ...form, shippingAddress: e.target.value })}
                        />
                    </div>

                    <div className="orders-row">
                        <div className="orders-field">
                            <label className="orders-label">City</label>
                            <input
                                className="orders-input"
                                type="text"
                                placeholder="Bloomington"
                                value={form.shippingCity}
                                onChange={(e) => setForm({ ...form, shippingCity: e.target.value })}
                            />
                        </div>
                        <div className="orders-field">
                            <label className="orders-label">State</label>
                            <input
                                className="orders-input"
                                type="text"
                                placeholder="IL"
                                value={form.shippingState}
                                onChange={(e) => setForm({ ...form, shippingState: e.target.value })}
                            />
                        </div>
                    </div>

                    <button
                        className="orders-place-btn"
                        type="submit"
                        disabled={placing}
                    >
                        {placing ? 'Placing Order...' : 'Place Order →'}
                    </button>
                </form>
            </div>

            {/* Order History */}
            <h3 className="orders-section-title">Order History</h3>

            {orders.length === 0 ? (
                <div className="orders-empty">
                    <p>No orders yet. Add items to cart and place your first order!</p>
                    <button
                        className="orders-shop-btn"
                        onClick={() => navigate('/products')}
                    >
                        Start Shopping
                    </button>
                </div>
            ) : (
                <div className="orders-list">
                    {orders.map(order => (
                        <div key={order.id} className="order-card">

                            <div className="order-card-header">
                                <div>
                                    <p className="order-number">{order.orderNumber}</p>
                                    <p className="order-date">
                                        {new Date(order.createdAt).toLocaleDateString()}
                                    </p>
                                </div>
                                <div className="order-card-right">
                                    <span
                                        className="order-status-badge"
                                        style={{
                                            background: getStatusColor(order.status) + '20',
                                            color: getStatusColor(order.status)
                                        }}
                                    >
                                        {order.status}
                                    </span>
                                    <p className="order-total">${order.totalAmount}</p>
                                </div>
                            </div>

                            <div className="order-items-list">
                                {order.orderItems?.map(item => (
                                    <p key={item.id} className="order-item-line">
                                        • {item.productTitle} × {item.quantity} — ${item.lineTotal}
                                    </p>
                                ))}
                            </div>

                            <p className="order-address">
                                {order.shippingAddress}, {order.shippingCity} {order.shippingState}
                            </p>

                        </div>
                    ))}
                </div>
            )}
        </div>
    );
}

export default Orders;