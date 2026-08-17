import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './css/Orders.css';

function Orders() {

    const [orders, setOrders] = useState([]);
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
        fetchOrders();
    }, []);

    // Fetch all orders for this user from Order Service
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

    // Status badge color
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

            {/* NOTE TO AKSHAYA: Add Place Order form here */}

            {/* Order History */}
            <h3 className="orders-section-title">Order History</h3>

            {orders.length === 0 ? (
                <div className="orders-empty">
                    <p>No orders yet.</p>
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