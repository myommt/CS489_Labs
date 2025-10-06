package edu.miu.cs.cs489.lab5;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.miu.cs.cs489.lab5.model.Surgery;
import edu.miu.cs.cs489.lab5.repository.SurgeryRepository;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class SurgeryIntegrationTest {

    @Autowired
    SurgeryRepository repo;

    @Test
    public void contextLoadsAndCrudWorks() {
    // Surgery constructor was updated to (name, location)
    Surgery s = new Surgery("Main St Dental","123 Main St");
        Surgery saved = repo.save(s);
        assertThat(saved.getId()).isNotNull();

        List<Surgery> all = repo.findAll();
        assertThat(all).isNotEmpty();
    }
}
