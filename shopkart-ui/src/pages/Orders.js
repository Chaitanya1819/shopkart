import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
// import './css/Orders.css';

function Orders() {

    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);
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
        try {
            const response = await fetch(`http://localhost:8083/api/orders/user/${userEmail}`, {
                headers: {
                    'Authorization': 'Bearer ' + token
                }
            });
            const data = await response.json();
            setOrders(data);
        } catch (error) {
            console.log('Cannot connect to Order Service');
        } finally {
            setLoading(false);
        }
    };

    if (loading) return <p>Loading orders...</p>;

    return (
        <div style={{ padding: '2rem' }}>

            <h2>My Orders</h2>
            <button onClick={() => navigate('/products')}>← Back to Products</button>

            {orders.length === 0 ? (
                <p>No orders yet.</p>
            ) : (
                orders.map(order => (
                    <div key={order.id} style={{ border: '1px solid #ddd', padding: '1rem', margin: '1rem 0', borderRadius: '8px' }}>
                        <p><strong>Order:</strong> {order.orderNumber}</p>
                        <p><strong>Status:</strong> {order.status}</p>
                        <p><strong>Total:</strong> ${order.totalAmount}</p>
                        <p><strong>Date:</strong> {new Date(order.createdAt).toLocaleDateString()}</p>
                        <p><strong>Address:</strong> {order.shippingAddress}, {order.shippingCity}</p>
                    </div>
                ))
            )}

        </div>
    );
}

export default Orders;