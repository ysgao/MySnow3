package org.ihtsdo.mysnow.importsct_ui;
/** Localizable strings for {@link org.ihtsdo.mysnow.importsct_ui}. */
class Bundle {
    /**
     * @return <i>Import</i>
     * @see ImportTopComponent
     */
    static String CTL_ImportAction() {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "CTL_ImportAction");
    }
    /**
     * @return <i>Import Window</i>
     * @see ImportTopComponent
     */
    static String CTL_ImportTopComponent() {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "CTL_ImportTopComponent");
    }
    /**
     * @return <i>Import window</i>
     * @see ImportTopComponent
     */
    static String HINT_ImportTopComponent() {
        return org.openide.util.NbBundle.getMessage(Bundle.class, "HINT_ImportTopComponent");
    }
    private Bundle() {}
}
