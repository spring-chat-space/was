package com.chat.was.global.crypto;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import com.chat.was.global.config.ApplicationContextProvider;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * AES-256 양방향 암호화 MyBatis TypeHandler.
 * MyBatis XML resultMap에서 email, phone_number 컬럼에 명시적으로 지정하여 사용.
 * MyBatis가 XML 파싱 및 결과 매핑 시 리플렉션을 통해 기본 생성자로 인스턴스를 생성하므로
 * 기본 생성자를 제공하고, 필요한 AES256Converter 빈은 지연 조회(Lazy Load)하여 사용한다.
 */
@Slf4j
public class AES256TypeHandler extends BaseTypeHandler<String> {

    private AES256Converter aes256Converter;

    /**
     * MyBatis 리플렉션 생성용 기본 생성자.
     */
    public AES256TypeHandler() {
    }

    /**
     * AES256Converter 빈을 지연 조회(Lazy-load)합니다.
     */
    private AES256Converter getConverter() {
        if (this.aes256Converter == null) {
            this.aes256Converter = ApplicationContextProvider.getBean(AES256Converter.class);
        }
        return this.aes256Converter;
    }

    /**
     * Java 값 → DB 컬럼 저장 시 암호화.
     *
     * @param ps         PreparedStatement
     * @param i          파라미터 인덱스
     * @param parameter  원문 문자열
     * @param jdbcType   JDBC 타입
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, getConverter().convertToDatabaseColumn(parameter));
    }

    /**
     * DB 컬럼명으로 ResultSet에서 값 조회 시 복호화.
     *
     * @param rs          ResultSet
     * @param columnName  컬럼명
     * @return 복호화된 원문 문자열
     */
    @Override
    public String getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return getConverter().convertToEntityAttribute(rs.getString(columnName));
    }

    /**
     * DB 컬럼 인덱스로 ResultSet에서 값 조회 시 복호화.
     *
     * @param rs           ResultSet
     * @param columnIndex  컬럼 인덱스
     * @return 복호화된 원문 문자열
     */
    @Override
    public String getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return getConverter().convertToEntityAttribute(rs.getString(columnIndex));
    }

    /**
     * CallableStatement에서 값 조회 시 복호화.
     *
     * @param cs           CallableStatement
     * @param columnIndex  컬럼 인덱스
     * @return 복호화된 원문 문자열
     */
    @Override
    public String getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return getConverter().convertToEntityAttribute(cs.getString(columnIndex));
    }
}
