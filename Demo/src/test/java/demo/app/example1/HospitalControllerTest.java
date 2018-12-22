package demo.app.example1;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import demo.app.DemoApplication;
import demo.app.example1.model.Hospital;

@SpringBootTest(classes= DemoApplication.class)
@RunWith(SpringRunner.class)
public class HospitalControllerTest {

	private MockMvc mockMvc;

	@Autowired
	WebApplicationContext context;

	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	public void testGetHospital() throws Exception {
		testAddHospital();
		mockMvc.perform(get("/test/hospitals/1")).andDo(print()).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Hospital One"))
				.andExpect(MockMvcResultMatchers.jsonPath("$.rating").value(4.1))
				.andExpect(MockMvcResultMatchers.jsonPath("$.city").value("Bangalore"));

	}

	@Test
	public void testAddHospital() throws Exception {
		Hospital hospital = new Hospital();
		hospital.setId(1);
		hospital.setName("Hospital One");
		hospital.setCity("Bangalore");
		hospital.setRating(4.1);
		byte[] hospJson = toJson(hospital);
		Hospital hospital_1 = new Hospital();
		hospital_1.setId(2);
		hospital_1.setName("Hospital Two");
		hospital_1.setCity("Mangalore");
		hospital_1.setRating(4.5);
		byte[] hospJson1 = toJson(hospital_1);
		mockMvc.perform(post("/test/hospitals/").content(hospJson).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
		mockMvc.perform(post("/test/hospitals/").content(hospJson1).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}

	@Test
	public void updateHospTest() throws Exception {
		Hospital hospital = new Hospital();
		hospital.setId(2);
		hospital.setName("Hospital Two");
		hospital.setCity("Mangalore");
		hospital.setRating(4.1); // update rating
		byte[] hospJson1 = toJson(hospital);
		mockMvc.perform(post("/test/hospitals/").content(hospJson1).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());

		mockMvc.perform(get("/test/hospitals/2")).andDo(print()).andExpect(status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.rating").value("4.1"));
	}

	@Test
	public void deleteHospTest() throws Exception {
		Hospital hospital = new Hospital();
		hospital.setId(1);		
		byte[] hospJson = toJson(hospital);
		mockMvc.perform(delete("/test/hospitals/").content(hospJson).contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

	}

	private byte[] toJson(Object r) throws Exception {
		ObjectMapper map = new ObjectMapper();
		return map.writeValueAsString(r).getBytes();
	}
}