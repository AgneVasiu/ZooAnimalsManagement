import React from "react";
import App from "../App";

export default function Navbar(){
    return(
        <div>
            <nav  class="navbar bg-dark border-bottom border-bottom-dark" data-bs-theme="dark">
                <div className="container-fluid">
                    <a className="navbar-brand" href="#">
                        Zoo Animals Management System
                    </a>
                    <button className="navbar-toggler" type="button" data-bs-toggle="collapse"
                            data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
                            aria-expanded="false" aria-label="Toggle navigation">
                        <button className="btn btn-outline-light"> Add Animal</button>
                        <button className="btn btn-outline-light"> Add Enclosure</button>
                    </button>
                </div>
            </nav>
        </div>
    )
}
