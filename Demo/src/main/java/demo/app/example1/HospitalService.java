package demo.app.example1;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class HospitalService {

	@Autowired
	private HospitalRepository hospitalRepository;

	public List<Hospital> getAllHospitals() {

		List<Hospital> hospitals = new ArrayList<Hospital>();
		hospitalRepository.findAll().forEach(hospitals::add);
		return hospitals;

	}

	public Hospital getHospital(int id) {
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
