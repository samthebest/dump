#!/bin/bash

brew install emacs
rustup component add rust-analyzer

# Create init.el if it doesn't exist
mkdir -p ~/.emacs.d

# MANUAL SHIT
# Open Terminal > Preferences > Profiles > Keyboard
# Check “Use Option as Meta key”

cat <<EOF > ~/.emacs.d/init.el

;;; Hello

;; Initialize package sources
(require 'package)
(setq package-enable-at-startup nil) ;; do not auto-load packages before init.el runs
(add-to-list 'package-archives '("gnu" . "https://elpa.gnu.org/packages/"))
(add-to-list 'package-archives '("melpa" . "https://melpa.org/packages/"))

(package-initialize)

(unless package-archive-contents
  (package-refresh-contents))

;; Ensure use-package is installed
(unless (package-installed-p 'use-package)
  (package-install 'use-package))

(require 'use-package)

(setq use-package-always-ensure t)

;; Better prompts
; (use-package vertico
;   :init
;   (vertico-mode))

;; Had to run `git clone https://github.com/minad/vertico.git ~/.emacs.d/vertico`
(add-to-list 'load-path "~/.emacs.d/vertico")
(require 'vertico)
(vertico-mode 1)

;; Rust mode
(use-package rust-mode
  :ensure t
  :mode "\\.rs\\'"
  :config)

;; Cargo minor mode - integrates cargo commands
(use-package cargo
  :ensure t
  :hook (rust-mode . cargo-minor-mode))

;; Flycheck for syntax checking
(use-package flycheck
  :ensure t
  :hook ((prog-mode . flycheck-mode)
         (lsp-mode . flycheck-mode)))

;; LSP mode for rust-analyzer
(use-package lsp-mode
  :ensure t
  :hook (rust-mode . lsp)
  :config
  (setq lsp-rust-analyzer-cargo-watch-command "clippy"
        lsp-diagnostics-provider :auto
        lsp-idle-delay 1.0
        lsp-rust-server 'rust-analyzer))

(setq lsp-log-io t)


;; Auto complete
(use-package company
  :ensure t
  :hook (after-init . global-company-mode)
  :bind (:map company-active-map
              ("<tab>" . company-complete-selection)
              ("TAB" . company-complete-selection))
        (:map company-mode-map
              ("<tab>" . company-indent-or-complete-common)
              ("TAB" . company-indent-or-complete-common)))

(setq tab-always-indent 'complete)

;; Alt Enter
(with-eval-after-load 'rust-mode
  (define-key rust-mode-map (kbd "M-RET") #'lsp-execute-code-action))

;; Reformat buffer
(with-eval-after-load 'rust-mode
  (define-key rust-mode-map (kbd "C-c C-f") #'lsp-format-buffer))

;; Rename
(with-eval-after-load 'rust-mode
  (define-key rust-mode-map (kbd "C-c <f6>") #'lsp-rename))

;; Inline variable
(defun lsp-rust-inline-action ()
  "Execute the Rust Analyzer inline refactor code action."
  (interactive)
  (lsp-request-async
   "textDocument/codeAction"
   (lsp--text-document-code-action-params)
   (lambda (actions)
     (let ((inline-action
            (seq-find (lambda (action)
                        (string= (gethash "kind" action) "refactor.inline"))
                      actions)))
       (if inline-action
           (lsp-execute-code-action inline-action)
         (message "No inline action available here"))))
   :error-handler #'lsp--error
   :mode 'detached))

(with-eval-after-load 'rust-mode
  (define-key rust-mode-map (kbd "M-v") #'lsp-rust-inline-action))


;; Supress unnecessary doc string warnings
(when (listp byte-compile-warnings)
  (setq byte-compile-warnings (remove 'docstrings byte-compile-warnings)))

;; Make it save buffers (layout)                     
(desktop-save-mode 1)

;; Globally save buffers (layout) TODO Make it work on a per-project basis
(setq desktop-path (list user-emacs-directory)) ; usually ~/.emacs.d/
(setq desktop-dirname user-emacs-directory)
(setq desktop-base-file-name "emacs.desktop")
(setq desktop-auto-save-timeout 10)  ; auto-save desktop every 2 seconds

(add-hook 'kill-emacs-hook #'desktop-save-in-desktop-dir)

(when (file-exists-p (expand-file-name "emacs.desktop" user-emacs-directory))
  (desktop-read user-emacs-directory))

(setq desktop-files-not-to-save nil)  ; save all buffers

(add-hook 'emacs-startup-hook 'treemacs)

;; Smooth scrolling
(setq scroll-margin 10)           ;; start scrolling when 5 lines from edge
(setq scroll-conservatively 101)  ;; don't recenter cursor unnecessarily
(setq scroll-step 1)              ;; scroll one line at a time
(setq auto-window-vscroll nil)    ;; disable slow pixel-based scrolling

;; Quickly reload init.el
(defun reload-init-file ()
  "Reload the Emacs init file."
  (interactive)
  (load user-init-file))

(global-set-key (kbd "C-c r") 'reload-init-file)

;; Edit init.el
(defun cf ()
  "Edit the `user-init-file`, which is usually ~/.emacs.d/init.el."
  (interactive)
  ;; (find-file user-init-file))
  (let ((buffer (find-file-noselect user-init-file)))
    (switch-to-buffer buffer)))

;; Directory viewer
(setq treemacs-width 70)

(use-package treemacs
  :ensure t
  :defer t
  :bind
  ("C-c t" . treemacs)
  :config)

;; Make copy paste actually work (in GUI apparently (not tested))
(setq select-enable-clipboard t)

;; Make copy paste actually work in Emacs
(setq interprogram-cut-function
      (lambda (text &optional _push)
        (let ((process-connection-type nil))
          (let ((proc (start-process "pbcopy" "*Messages*" "pbcopy")))
            (process-send-string proc text)
            (process-send-eof proc)))))

(setq interprogram-paste-function
      (lambda ()
        (let ((text (shell-command-to-string "pbpaste")))
          (unless (string= text "")
            text))))

;; Delete line
(global-set-key (kbd "C-c x") 'kill-whole-line)

;; Copy text
(global-set-key (kbd "C-c c") 'kill-ring-save)

;; Dupe line
(defun duplicate-line()
  "Duplicate the current line."
  (interactive)
  (move-beginning-of-line 1)
  (kill-ring-save (point) (line-end-position))
  (end-of-line)
  (open-line 1)
  (next-line 1)
  (yank))

(global-set-key (kbd "C-c d") 'duplicate-line)

;; Auto save
(setq auto-save-timeout 1)
(setq auto-save-interval 1)
(setq auto-save-default t)

(run-with-idle-timer 1 t (lambda () (save-some-buffers t)))

;; Remove indentation
(defun unindent-region-by-one-level (start end)
  "Unindent selected region by one standard indent level for the current major mode."
  (interactive "r")
  (let ((indent (or (and (boundp 'c-basic-offset) c-basic-offset)
                    (and (boundp 'lisp-body-indent) lisp-body-indent)
                    (and (boundp 'rust-indent-offset) rust-indent-offset)
                    tab-width
                    4)))
    (indent-rigidly start end (- indent))))

(global-set-key (kbd "<backtab>") 'unindent-region-by-one-level)


;; Add indentation
(defun indent-region-by-one-level ()
  "Indent selected region by one standard indent level for the current major mode."
  (interactive)
  (let ((indent (or (and (boundp 'c-basic-offset) c-basic-offset) ; c-like modes
                    (and (boundp 'lisp-body-indent) lisp-body-indent) ; elisp
                    (and (boundp 'rust-indent-offset) rust-indent-offset) ; rust-mode
                    tab-width ; fallback
                    4))) ; default fallback
    (indent-rigidly (region-beginning) (region-end) indent)))

(global-set-key (kbd "C-TAB") 'indent-region-by-one-level)

;; Brute force make selection fucking work
(defun my/keep-region-active ()
  "Keep region active after most commands, except when cutting, copying, or deleting."
  (when (and (region-active-p)
             (not (memq this-command
                        '(kill-region
                          kill-ring-save
                          clipboard-kill-region
                          clipboard-kill-ring-save
                          delete-region
                          delete-char
                          backward-delete-char
                          delete-forward-char
                          delete-backward-char))))
    (setq deactivate-mark nil)))

(add-hook 'post-command-hook #'my/keep-region-active)

;; Find file in git repo (or project)
(global-set-key (kbd "C-c n") #'project-find-file)

;; Multi-cursors
(use-package multiple-cursors
  :ensure t
  :bind (("C-c g" . mc/mark-next-like-this)
         ("C-c h" . mc/mark-previous-like-this)
         ("C-c a" . mc/mark-all-like-this)))

;; Comment current line
(defun comment-or-uncomment-current-line ()
  "Comment or uncomment the current line."
  (interactive)
  (let ((beg (line-beginning-position))
        (end (line-end-position)))
    (comment-or-uncomment-region beg end)))

(global-set-key (kbd "C-c ;") #'comment-or-uncomment-current-line)

;; Save backups to
(setq backup-directory-alist
      `(("." . ,(concat user-emacs-directory ".emacs-backups"))))

;;
;; Treemacs
;;
(with-eval-after-load 'treemacs
  (define-key treemacs-mode-map (kbd "a") #'treemacs-create-file)
  (define-key treemacs-mode-map (kbd "+") #'treemacs-create-dir))

;;
;; RUST Specific shortcuts
;;
(defun my/treemacs-create-module (module-name)
  "Create a new Rust module named MODULE-NAME in the Treemacs-selected directory.
Also appends `pub mod <name>;` to mod.rs in that directory."
  (interactive "sModule name: ")
  (require 'treemacs)
  (let* ((btn (treemacs-current-button))
         (path (treemacs-button-get btn :path))
         (dir (if (file-directory-p path)
                  path
                (file-name-directory path)))
         (new-file (expand-file-name (concat module-name ".rs") dir))
         (mod-file (expand-file-name "mod.rs" dir)))
    ;; Create the new file if it doesn't exist
    (unless (file-exists-p new-file)
      (with-temp-buffer (write-file new-file)))
    ;; Add line to mod.rs
    (with-current-buffer (find-file-noselect mod-file)
      (goto-char (point-max))
      (insert (format "\npub mod %s;" module-name))
      (save-buffer))
    (message "Created %s and updated mod.rs" new-file)))

(global-set-key (kbd "C-c m") #'my/treemacs-create-module)



;;
;; Auto Generated stuff
;;
(custom-set-variables
 ;; custom-set-variables was added by Custom.
 ;; If you edit it by hand, you could mess it up, so be careful.
 ;; Your init file should contain only one such instance.
 ;; If there is more than one, they won't work right.
 '(package-selected-packages
   '(cargo company flycheck lsp-ui multiple-cursors rust-mode treemacs
	   vertico)))
(custom-set-faces
 ;; custom-set-faces was added by Custom.
 ;; If you edit it by hand, you could mess it up, so be careful.
 ;; Your init file should contain only one such instance.
 ;; If there is more than one, they won't work right.
 )


EOF

