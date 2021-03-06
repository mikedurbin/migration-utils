package org.fcrepo.migration.idmapers;

import org.fcrepo.migration.MigrationIDMapper;

/**
 * A simple MigrationIDMapper that converts pids to
 * paths using the following pattern:
 *
 * [namespace]/[01]/[23]/[45]/[56] where [namespace] is
 * the pid namespace, [01] is the first two characters in
 * the remainder of the pid, etc.
 *
 * This implementation ensures easy reversibility in mapping,
 * reasonable use of the hierarchy,
 * @author mdurbin
 */
public class SimpleIDMapper implements MigrationIDMapper {

    private String rootPath;

    private int charDepth;

    /**
     * simple ID mapper.
     * @param rootPath the root path
     */
    public SimpleIDMapper(final String rootPath) {
        this.rootPath = rootPath;
        charDepth = 2;
    }

    /**
     * Sets the number of characters to use per level when converting
     * a pid to a path.  If it is known that all pids will be numeric,
     * a value or 3 would result in levels containing no more than 1000
     * children.
     * @param charDepth the number of characters to include in the
     *                  path segments created from the pid.
     */
    public void setCharDepth(final int charDepth) {
        if (charDepth < 1) {
            throw new IllegalArgumentException();
        }
        this.charDepth = charDepth;
    }

    /**
     * Gets the number of characters to use per level when converting
     * a pid to a path.
     */
    public int getCharDepth() {
        return this.charDepth;
    }

    @Override
    public String mapObjectPath(final String pid) {
        return pidToPath(pid);
    }

    private String pidToPath(final String pid) {
        final StringBuffer path = new StringBuffer();
        path.append(rootPath);
        if (!rootPath.endsWith("/")) {
            path.append("/");
        }
        path.append(pid.substring(0, pid.indexOf(':')));
        for (int i = pid.indexOf(':') + 1; i < pid.length(); i += charDepth) {
            path.append('/');
            path.append(pid.substring(i, Math.min(i + charDepth, pid.length())));
        }
        return path.toString();
    }

    @Override
    public String mapDatastreamPath(final String pid, final String dsid) {
        return pidToPath(pid) + '/' + dsid;
    }
}
