import './App.css';
import 'bootstrap/dist/css/bootstrap.css';
import Navbar from "./layout/Navbar";
import Home from "./pages/Home";
import { BrowserRouter as Router, Route} from 'react-router-dom';

function App() {
    return (
        <div className="App">
            <Navbar />
            <Home />
        </div>
    );
}

export default App;
