import React, {useEffect, useState} from "react";
import axios from "axios";

export default function Home(){
    const [animals, setAnimals]=useState([])
    useEffect(() =>{
        loadAnimals();

    },[]);
    const loadAnimals=async()=>{
        const result=await axios.get("http://localhost:8080/animals")
        setAnimals(result.data);
    };
    return(
        <div className='container'>
            <div className='py-4'>
                <table className="table border shadow">
                    <thead>
                    <tr>
                        <th scope="col">#</th>
                        <th scope="col">Species</th>
                        <th scope="col">Food</th>
                        <th scope="col">Amount</th>
                        <th scope="col">Enclosure ID</th>
                        <th scope="col">Action</th>

                    </tr>
                    </thead>
                    <tbody>
                    {
                        animals.map((animals,index)=>(
                            <tr>
                                <th scope="row"key={index}>{index+1}</th>
                                <td>{animals.species}</td>
                                <td>{animals.food}</td>
                                <td>{animals.amount}</td>
                                <td>{animals.enclosureId}</td>
                                <td>
                                    <button className='btn btn-primary mx-2'>View</button>
                                    <button className='btn btn-outline-primary mx-2'>Edit</button>
                                    <button className='btn btn-danger mx-2'>Delete</button>
                                </td>
                            </tr>
                        ))
                    }
                    </tbody>
                </table>
            </div>
        </div>
    )
}