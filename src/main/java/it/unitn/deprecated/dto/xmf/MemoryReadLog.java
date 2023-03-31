package it.unitn.deprecated.dto.xmf;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Atefeh Zareh Chahoki
 * change format from (executeDate) instruction readAddress readCount readContent
 * such as:
 * (2016/01/12, 09:08:24) 000007FB6E23028F: R 000007FB6E33CB00 8 0x7fb6ca21828
 * <p>
 * to:
 * readCount readContent
 * 8 0x7fb6ca21828
 * <p>
 * for making the monit module more efficient.
 * todo we should decide later about adding time and removing the count.
 **/
@Setter
@Getter
@AllArgsConstructor
@ToString
public class MemoryReadLog {
    private String readCount;
    private String readContent;
}
