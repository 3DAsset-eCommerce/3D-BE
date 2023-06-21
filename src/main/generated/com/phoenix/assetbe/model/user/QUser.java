package com.phoenix.assetbe.model.user;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -573133022L;

    public static final QUser user = new QUser("user");

    public final com.phoenix.assetbe.core.util.QMyTimeBaseUtil _super = new com.phoenix.assetbe.core.util.QMyTimeBaseUtil(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdAt = _super.createdAt;

    public final StringPath email = createString("email");

    public final StringPath emailCheckToken = createString("emailCheckToken");

    public final BooleanPath emailVerified = createBoolean("emailVerified");

    public final StringPath firstName = createString("firstName");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath lastName = createString("lastName");

    public final StringPath password = createString("password");

    public final EnumPath<SocialType> provider = createEnum("provider", SocialType.class);

    public final StringPath reason = createString("reason");

    public final StringPath role = createString("role");

    public final EnumPath<Status> status = createEnum("status", Status.class);

    public final DateTimePath<java.time.LocalDateTime> tokenCreatedAt = createDateTime("tokenCreatedAt", java.time.LocalDateTime.class);

    public final DateTimePath<java.time.LocalDateTime> updatedAt = createDateTime("updatedAt", java.time.LocalDateTime.class);

    public QUser(String variable) {
        super(User.class, forVariable(variable));
    }

    public QUser(Path<? extends User> path) {
        super(path.getType(), path.getMetadata());
    }

    public QUser(PathMetadata metadata) {
        super(User.class, metadata);
    }

}

