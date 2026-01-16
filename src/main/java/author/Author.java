package author;

import com.booklover.book_lover_community.book.Book;
import com.booklover.book_lover_community.userBook.UserBook;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Setter
@Getter
@NoArgsConstructor
@Table(name = "author")
public class Author {

    @Id
    private Long id;

    @Column(nullable = false, length = 100)
    private String fullName;

    @OneToMany(fetch =LAZY, mappedBy = "book", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Book> userBooks;

}


