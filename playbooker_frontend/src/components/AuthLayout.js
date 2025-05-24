import React from 'react'
import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { isUserLoggedIn } from '../utils/tokenUtil';

const AuthLayout = () => {
    const location = useLocation(); 

    return (
    // replace: prevents user from going back to the protected route via back button.
    // Outlet: If the token exists, render the child route under it: This is like a placeholder for nested routes defined in your Routes.
    isUserLoggedIn() ? <Outlet /> : <Navigate to="/login" state={{ from: location }} replace />
  )
}

export default AuthLayout