package pl.edu.agh.pawicao.studying_squirrels_api.model.node;

import lombok.Data;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;
import pl.edu.agh.pawicao.studying_squirrels_api.model.relationship.SemwebEntityConnection;

import java.util.ArrayList;
import java.util.List;

@NodeEntity
@Data
public class SemwebEntity {

    @Id
    private String uri;

    private String name;
    private String wikipediaUrl;

    @Relationship(type = "IS_RELATED")
    private List<SemwebEntityConnection> relatedEntities = new ArrayList<>();

    @Relationship(type = "IS_RELATED", direction = "INCOMING")
    private List<SemwebEntityConnection> relatedEntitiesIncoming = new ArrayList<>();

}
