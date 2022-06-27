package pl.edu.agh.pawicao.studying_squirrels_api.model.relationship;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import org.neo4j.ogm.annotation.*;
import pl.edu.agh.pawicao.studying_squirrels_api.model.node.SemwebEntity;

@RelationshipEntity(type = "IS_RELATED")
@Getter
@Setter
public class SemwebEntityConnection {

    @Id
    @GeneratedValue
    private Long id;

    @JsonIgnoreProperties({"relatedEntities", "relatedEntitiesIncoming"})
    @StartNode
    private SemwebEntity startEntity;

    @JsonIgnoreProperties({"relatedEntities", "relatedEntitiesIncoming"})
    @EndNode
    private SemwebEntity endEntity;

    private Integer shortestDistance;
    private Integer numberOfConnections;
}
