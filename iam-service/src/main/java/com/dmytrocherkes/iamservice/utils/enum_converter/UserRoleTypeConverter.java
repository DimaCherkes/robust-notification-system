package com.dmytrocherkes.iamservice.utils.enum_converter;

import com.dmytrocherkes.iamservice.service.model.IamServiceUserRole;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class UserRoleTypeConverter implements AttributeConverter<IamServiceUserRole, String> {

    @Override
    public String convertToDatabaseColumn(IamServiceUserRole iamServiceUserRole) {
        return iamServiceUserRole.name();
    }

    @Override
    public IamServiceUserRole convertToEntityAttribute(String dbData) {
        return IamServiceUserRole.fromName(dbData);
    }
}
