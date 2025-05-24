import { Navigate, useLocation } from "react-router-dom";
import { isUserLoggedIn } from "../utils/tokenUtil";

const PublicOnly = ({ children }) => {
  
  const location = useLocation();

  if (isUserLoggedIn()) {
    // Redirect to the page user came from or default to /dashboard
    console.log(location)
    const from = location.state?.from?.pathname || "/home";
    return <Navigate to={from} replace />;
  }

  return children;
};

export default PublicOnly;
