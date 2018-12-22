package demo.app.example1.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import demo.app.example1.HospRepository;
import demo.app.example1.model.Hospital;

@Service
public class HospService {

	@Autowired
	private HospRepository hospitalRepository;

	public List<Hospital> getAllHospitals() {

		List<Hospital> hospitals = new ArrayList<Hospital>();
		hospitalRepository.findAll().forEach(hospitals::add);
		return hospitals;

	}

	public Hospital getHospital(int id) {
		// return hospitalRepository.findOne(id);
		return hospitalRepository.findById(id).get();  
	}

	public void addHospital(Hospital hospital) {
		hospitalRepository.save(hospital);
	}

	public void updateHospital(Hospital hospital) {
		hospitalRepository.save(hospital);

	}

	public void deleteHospital(Hospital hospital) {
		hospitalRepository.delete(hospital);
	}
}
