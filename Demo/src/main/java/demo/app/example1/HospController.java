package demo.app.example1;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import demo.app.example1.model.Hospital;
import demo.app.example1.service.HospService;

@RestController
/*@RequestMapping("/test/")*/
public class HospController {

	private static final Logger logger = LoggerFactory.getLogger(HospController.class);

	@Autowired
	private HospService hospitalService;

	@RequestMapping("/test/hospitals/{id}")
	public @ResponseBody Hospital getHospital(@PathVariable("id") int id) throws Exception {
		logger.info("Get hospital with id {}", id);
		return hospitalService.getHospital(id);
	}

	@RequestMapping("/test/hospitals")
	public @ResponseBody List<Hospital> getAllHospitals() throws Exception {
		logger.info("Get all hospitals");
		return hospitalService.getAllHospitals();
	}

	@RequestMapping(method = RequestMethod.POST, path = "/test/hospitals/")
	public ResponseEntity<String> addHospital(@RequestBody Hospital hospital) {
		logger.info("Save {}", hospital);
		hospitalService.addHospital(hospital);
		return null;
	}

	@RequestMapping(method = RequestMethod.PUT, path = "/test/hospitals/")
	public ResponseEntity<String> updateHospital(@RequestBody Hospital hospital) {
		logger.info("Update {} ", hospital);
		hospitalService.updateHospital(hospital);
		return null;
	}

	@RequestMapping(method = RequestMethod.DELETE, path = "/test/hospitals/")
	public ResponseEntity<String> deleteHospital(@RequestBody Hospital hospital) {
		logger.info("Delete {} ", hospital);
		hospitalService.deleteHospital(hospital);

		return new ResponseEntity<String>("Demo", null, HttpStatus.NO_CONTENT);

	}



}
