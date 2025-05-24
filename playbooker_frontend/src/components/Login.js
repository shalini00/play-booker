import React from 'react';
import { useState } from 'react';
import { useNavigate } from "react-router-dom";
import '../App.css';
import { saveToken } from '../utils/tokenUtil';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const navigate = useNavigate();

    const handleLogin = async (e) => {
        e.preventDefault();
        try {
            const response = await fetch("http://localhost:8080/users/login", {
                method: "POST",
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ email, password }),
                credentials: 'include'
            });

            if (!response.ok) {
                throw new Error("Login failed");
            }

            const data = await response.json();
            saveToken(data);
            navigate("/home");
        } catch (error) {
            alert(error.message);
        }
    };


    const googleLogin = () => {
        const CLIENT_ID = '710764702629-0o0em1nt4e954898g63qn3b51qn9mrj4.apps.googleusercontent.com';
        const REDIRECT_URI = 'http://localhost:3000/oauthcallback';
        const SCOPE = 'openid email profile';
        const RESPONSE_TYPE = 'code';
        const PROMPT = 'consent';

        window.location.href = `https://accounts.google.com/o/oauth2/v2/auth?client_id=${CLIENT_ID}&redirect_uri=${REDIRECT_URI}&response_type=${RESPONSE_TYPE}&scope=${encodeURIComponent(SCOPE)}&prompt=${PROMPT}`;
    }

    return (
        <div style={styles.container}>
            <form onSubmit={handleLogin} style={styles.form}>
                <h2>Login</h2>
                <input
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    style={styles.input}
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    style={styles.input}
                />
                <button type="submit" style={styles.button}>Login</button>

                <div style={{ margin: '20px 0' }}>OR</div>
                <div id="googleSignInDiv" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', border: '1px solid grey', padding: '5px', borderRadius: '5px'}} onClick={googleLogin}>
                    <img src="https://www.gstatic.com/firebasejs/ui/2.0.0/images/auth/google.svg" alt="Google logo" width="16" height="16" style={{ marginRight: '5px' }} />
                    Sign in With Google
                </div>
            </form>
        </div>
    );
};

const styles = {
    container: {
        display: 'flex',
        justifyContent: 'center',
        alignItems: 'center',
        height: '100vh',
        backgroundColor: '#f0f2f5',
    },
    form: {
        backgroundColor: '#fff',
        padding: 30,
        borderRadius: 10,
        boxShadow: '0 0 10px rgba(0,0,0,0.1)',
        minWidth: 300,
        textAlign: 'center',
    },
    input: {
        width: '100%',
        padding: 10,
        margin: '10px 0',
        fontSize: 16,
    },
    button: {
        width: '100%',
        padding: 10,
        fontSize: 16,
        backgroundColor: '#4CAF50',
        color: 'white',
        border: 'none',
        borderRadius: 5,
        cursor: 'pointer',
    },
    googleButtonStyle: {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        gap: '10px',
        backgroundColor: '#ffffff',
        color: '#555',
        border: '1px solid #ddd',
        borderRadius: '5px',
        padding: '10px 16px',
        fontSize: '14px',
        fontWeight: 500,
        cursor: 'pointer',
        boxShadow: '0 2px 4px rgba(0, 0, 0, 0.1)',
        transition: 'background-color 0.2s ease, box-shadow 0.2s ease',
    },

    googleLogoStyle: {
        height: '18px',
        width: '18px',
    }


};

export default Login;
