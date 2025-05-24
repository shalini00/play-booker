import './App.css';
import Login from './components/Login';
import NotFound from "./components/NotFound";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";
import OAuthCallback from './components/OAuthCallback';
import Dashboard from './components/Dashboard';
import AuthLayout from './components/AuthLayout';
import PublicOnly from './components/PublicOnly';
import BookingForm from './components/BookingForm';
import PaymentSucess from './components/PaymentSucess';

function App() {
  return (
    <Router>
      <Routes>
      <Route path="/" element={< Dashboard/>} />
        <Route path="/login" element={
          <PublicOnly>
            <Login />
          </PublicOnly>
          } />
       
        <Route path="/oauthcallback" element={<OAuthCallback />} />
        
        <Route element={<AuthLayout />}>
          <Route path="/home" element={<BookingForm />} />
          <Route path='/payment-success' element={<PaymentSucess />} />
          <Route path="*" element={<NotFound />} />
        </Route>
      </Routes>
    </Router>
  );
}

export default App;
