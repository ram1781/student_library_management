package library_project.dto;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class BorrowerData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    
    // Use java.util.Date for borrow_date and submit_date
    @Temporal(TemporalType.DATE)  // This ensures that only the date (not the time) is stored
    private Date borrowDate;
    
    @Temporal(TemporalType.DATE)
    private Date submitDate;

    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<Book> book;

    @ManyToMany(cascade = CascadeType.PERSIST)
    private List<Student> student;
}
