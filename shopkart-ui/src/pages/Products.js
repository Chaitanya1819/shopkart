import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './css/Products.css';

function Products() {

    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);
    const [search, setSearch] = useState('');
    const [serverError, setServerError] = useState('');
    const [toast, setToast] = useState('');
    const navigate = useNavigate();

    const userEmail = localStorage.getItem('userEmail');
    const userName = localStorage.getItem('userName');
    const token = localStorage.getItem('token');

    // Check if logged in
    useEffect(() => {
        if (!token) {
            navigate('/login');
            return;
        }
        fetchProducts();
    }, []);

    // Fetch all products from Product Service
    const fetchProducts = async () => {
        setLoading(true);
        try {
            const response = await fetch('http://localhost:8081/api/products', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                }
            });

            if (response.ok) {
                const data = await response.json();
                setProducts(data);
            } else {
                setServerError('Failed to load products.');
            }

        } catch (error) {
            setServerError('Cannot connect to Product Service. Make sure it is running on port 8081.');
        } finally {
            setLoading(false);
        }
    };

    // Add item to cart
    const addToCart = async (product) => {
        try {
            const response = await fetch('http://localhost:8082/api/cart/add', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': 'Bearer ' + token
                },
                body: JSON.stringify({
                    userEmail: userEmail,
                    productId: product.id,
                    quantity: 1
                })
            });

            if (response.ok) {
                showToast('Added to cart: ' + product.title.slice(0, 30) + '...');
            } else {
                showToast('Failed to add to cart');
            }

        } catch (error) {
            showToast('Cannot connect to Cart Service on port 8082.');
        }
    };

    // Show toast message
    const showToast = (message) => {
        setToast(message);
        setTimeout(() => setToast(''), 3000);
    };

    // Logout
    const handleLogout = () => {
        localStorage.clear();
        navigate('/login');
    };

    // Filter products by search
    const filtered = products.filter(p =>
        p.title?.toLowerCase().includes(search.toLowerCase()) ||
        p.brand?.toLowerCase().includes(search.toLowerCase())
    );

    // Loading state
    if (loading) {
        return (
            <div className="products-loading">
                <p>Loading 240 products...</p>
            </div>
        );
    }

    return (
        <div className="products-page">

            {/* Navbar */}
            <div className="products-navbar">
                <h2 className="products-navbar-title">ShopKart</h2>
                <div className="products-navbar-right">
                    <span className="products-welcome">Hello, {userName}!</span>
                    <button
                        className="products-nav-btn"
                        onClick={() => navigate('/cart')}
                    >
                        Cart
                    </button>
                    <button
                        className="products-nav-btn-outline"
                        onClick={() => navigate('/orders')}
                    >
                        Orders
                    </button>
                    <button
                        className="products-nav-btn-outline"
                        onClick={handleLogout}
                    >
                        Logout
                    </button>
                </div>
            </div>

            {/* Server error */}
            {serverError && (
                <div className="products-server-error">
                    {serverError}
                </div>
            )}

            {/* Search bar */}
            <input
                type="text"
                className="products-search"
                placeholder="Search products or brands..."
                value={search}
                onChange={(e) => setSearch(e.target.value)}
            />

            <p className="products-count">
                Showing {filtered.length} products
            </p>

            {/* Product grid */}
            <div className="products-grid">
                {filtered.map(product => (
                    <div key={product.id} className="product-card">

                        <img
                            src={product.imageUrl}
                            alt={product.title}
                            className="product-card-img"
                            onError={(e) => e.target.style.display = 'none'}
                        />

                        <div className="product-card-info">
                            <p className="product-card-brand">{product.brand}</p>
                            <p className="product-card-title">
                                {product.title?.slice(0, 45)}...
                            </p>
                            <p className="product-card-price">
                                ${product.discountedPrice}
                            </p>
                            {product.price && (
                                <p className="product-card-mrp">
                                    ${product.price}
                                </p>
                            )}
                            <button
                                className="product-card-btn"
                                onClick={() => addToCart(product)}
                            >
                                + Add to Cart
                            </button>
                        </div>

                    </div>
                ))}
            </div>

            {/* Toast notification */}
            {toast && (
                <div className="products-toast">
                    {toast}
                </div>
            )}

        </div>
    );
}

export default Products;